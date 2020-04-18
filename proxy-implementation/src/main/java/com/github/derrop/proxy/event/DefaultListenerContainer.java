package com.github.derrop.proxy.event;

import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.event.EventPriority;
import com.github.derrop.proxy.api.event.ListenerContainer;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DefaultListenerContainer implements ListenerContainer {

    DefaultListenerContainer(PluginContainer pluginContainer, Class<?> eventClassTarget, Object listenerInstance, Method method, EventPriority priority) {
        this.pluginContainer = pluginContainer;
        this.eventClassTarget = eventClassTarget;
        this.listenerInstance = listenerInstance;
        this.method = method;
        this.priority = priority;
    }

    private final PluginContainer pluginContainer;

    private final Class<?> eventClassTarget;

    private final Object listenerInstance;

    private final Method method;

    private final EventPriority priority;

    @Override
    public @Nullable PluginContainer getPlugin() {
        return this.pluginContainer;
    }

    @Override
    public @NotNull Object getListenerInstance() {
        return this.listenerInstance;
    }

    @Override
    public @NotNull Class<?> getTargetEventClass() {
        return this.eventClassTarget;
    }

    @Override
    public @NotNull EventPriority getPriority() {
        return this.priority;
    }

    @Override
    public void call(@NotNull Event event) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(this.listenerInstance, event);
    }
}
