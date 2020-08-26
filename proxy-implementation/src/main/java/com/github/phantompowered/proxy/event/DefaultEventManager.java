/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.event;

import com.github.phantompowered.proxy.api.event.Event;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.event.ListenerContainer;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.logging.ProxyLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class DefaultEventManager implements EventManager {

    private final Logger logger;
    private final List<ListenerContainer> registeredListeners = new CopyOnWriteArrayList<>();

    public DefaultEventManager(ServiceRegistry registry) {
        this.logger = registry.getProviderUnchecked(ProxyLogger.class);
    }

    @Override
    public void callEvent(@NotNull Class<? extends Event> event) {
        try {
            callEvent(event.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull <T extends Event> T callEvent(@NotNull T event) {
        event.preCall();

        for (ListenerContainer registeredListener : this.registeredListeners) {
            if (registeredListener.getTargetEventClass().equals(event.getClass())) {
                try {
                    this.logger.fine("Posting event " + event.getClass().getName() + " to listener " + registeredListener.getListenerInstance().getClass().getName());
                    registeredListener.call(event);
                } catch (final InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        event.postCall();
        return event;
    }

    @Override
    public void registerListener(@Nullable PluginContainer pluginContainer, @NotNull Object listener) {
        for (Method method : listener.getClass().getMethods()) {
            Listener annotation = method.getAnnotation(Listener.class);
            if (annotation == null) {
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1) {
                System.err.println("Unable to register listener which has more than one parameter");
                continue;
            }

            if (!Event.class.isAssignableFrom(parameters[0])) {
                System.err.println("Cannot register listener which has no event as parameter");
                continue;
            }

            ListenerContainer container = new DefaultListenerContainer(
                    pluginContainer,
                    parameters[0],
                    listener,
                    method,
                    annotation.priority()
            );
            this.registeredListeners.add(container);
        }

        this.registeredListeners.sort(Comparator.comparingInt(t0 -> t0.getPriority().getPriority()));
    }

    @Override
    public void registerListener(@Nullable PluginContainer pluginContainer, @NotNull Class<?> listener) {
        try {
            this.registerListener(pluginContainer, listener.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void unregisterListener(@NotNull Object listener) {
        this.registeredListeners.removeIf(e -> e.getListenerInstance() == listener);
    }

    @Override
    public void unregisterAll(@NotNull PluginContainer pluginContainer) {
        this.registeredListeners.removeIf(e -> e.getPlugin() != null && e.getPlugin() == pluginContainer);
    }

    @Override
    public void unregisterAll() {
        this.registeredListeners.clear();
    }

    @Override
    public @NotNull Collection<ListenerContainer> getRegisteredListeners(@NotNull PluginContainer pluginContainer) {
        return this.registeredListeners
                .stream()
                .filter(e -> e.getPlugin() != null && e.getPlugin() == pluginContainer)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Collection<ListenerContainer> getRegisteredListeners() {
        return Collections.unmodifiableCollection(this.registeredListeners);
    }
}
