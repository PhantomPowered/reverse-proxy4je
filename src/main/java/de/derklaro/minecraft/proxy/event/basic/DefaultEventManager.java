package de.derklaro.minecraft.proxy.event.basic;

import de.derklaro.minecraft.proxy.event.Event;
import de.derklaro.minecraft.proxy.event.EventManager;
import de.derklaro.minecraft.proxy.event.LoadedListener;
import de.derklaro.minecraft.proxy.event.handler.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DefaultEventManager implements EventManager {

    private final Lock lock = new ReentrantLock();

    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();

    private final Map<Class<?>, List<LoadedListener>> done = new ConcurrentHashMap<>();

    @Override
    public void callEvent(Class<? extends Event> event) {
        try {
            callEvent(event.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull <T extends Event> T callEvent(T event) {
        event.preCall();

        List<LoadedListener> listeners = done.get(event.getClass());
        if (listeners != null) {
            listeners.forEach(loadedListener -> {
                if (!event.isAsync()) {
                    try {
                        loadedListener.call(event);
                    } catch (final InvocationTargetException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    CompletableFuture.runAsync(() -> {
                        try {
                            loadedListener.call(event);
                        } catch (final InvocationTargetException | IllegalAccessException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            });
        }

        event.postCall();
        return event;
    }

    @Override
    public void callEventAsync(Class<? extends Event> event) {
        CompletableFuture.runAsync(() -> callEvent(event));
    }

    @Override
    public void callEventAsync(Event event) {
        CompletableFuture.runAsync(() -> callEvent(event));
    }

    @Override
    public void registerListener(Object listener) {
        this.register(listener);
    }

    @Override
    public void registerListener(Class<?> listener) {
        try {
            this.register(listener.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void registerListenerAsync(Object listener) {
        CompletableFuture.runAsync(() -> registerListener(listener));
    }

    @Override
    public void registerListenerAsync(Class<?> listener) {
        CompletableFuture.runAsync(() -> registerListener(listener));
    }

    @Override
    public void unregisterListener(Object listener) {
        unregister(listener);
    }

    @Override
    public void unregisterAll() {
        for (List<LoadedListener> value : this.done.values()) {
            for (LoadedListener loadedListener : value) {
                this.unregister(loadedListener.getListener());
            }
        }
    }

    @NotNull
    @Override
    public List<List<LoadedListener>> getListeners() {
        return new ArrayList<>(this.done.values());
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> find(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> result = new HashMap<>();
        for (Method method : listener.getClass().getDeclaredMethods()) {
            Listener annotation = method.getAnnotation(Listener.class);
            if (annotation != null) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1) {
                    continue;
                }

                Map<Byte, Set<Method>> map = result.computeIfAbsent(parameters[0], aClass -> new HashMap<>());
                Set<Method> methods = map.computeIfAbsent(annotation.priority().getPriority(), aByte -> new HashSet<>());

                methods.add(method);
            }
        }

        return result;
    }

    private void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handlers = find(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> classMapEntry : handlers.entrySet()) {
                Map<Byte, Map<Object, Method[]>> priorities = byListenerAndPriority.computeIfAbsent(classMapEntry.getKey(), aClass -> new HashMap<>());

                for (Map.Entry<Byte, Set<Method>> byteSetEntry : classMapEntry.getValue().entrySet()) {
                    Map<Object, Method[]> current = priorities.computeIfAbsent(byteSetEntry.getKey(), aByte -> new HashMap<>());

                    Method[] methods = new Method[byteSetEntry.getValue().size()];
                    current.put(listener, byteSetEntry.getValue().toArray(methods));
                }

                finallyDo(classMapEntry.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    private void finallyDo(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> map = byListenerAndPriority.get(eventClass);
        if (map != null) {
            List<LoadedListener> listeners = new ArrayList<>();

            byte current = Byte.MIN_VALUE;
            do {
                Map<Object, Method[]> handlers = map.get(current);
                if (handlers != null) {
                    for (Map.Entry<Object, Method[]> objectEntry : handlers.entrySet()) {
                        for (Method method : objectEntry.getValue()) {
                            LoadedListener loadedListener = new LoadedListener(objectEntry.getKey(), method);
                            listeners.add(loadedListener);
                        }
                    }
                }
            } while (current++ < Byte.MAX_VALUE);
            done.put(eventClass, listeners);
        } else {
            done.remove(eventClass);
        }
    }

    private void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = find(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> classMapEntry : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.get(classMapEntry.getKey());
                if (prioritiesMap != null) {
                    for (Byte aByte : classMapEntry.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(aByte);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (currentPriority.isEmpty()) {
                                prioritiesMap.remove(aByte);
                            }
                        }
                    }

                    if (prioritiesMap.isEmpty()) {
                        byListenerAndPriority.remove(classMapEntry.getKey());
                    }
                }

                finallyDo(classMapEntry.getKey());
            }
        } finally {
            lock.unlock();
        }
    }
}
