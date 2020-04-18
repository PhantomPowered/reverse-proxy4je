package com.github.derrop.proxy.api.event;

import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public interface ListenerContainer {

    @Nullable PluginContainer getPlugin();

    @NotNull Object getListenerInstance();

    @NotNull Class<?> getTargetEventClass();

    @NotNull EventPriority getPriority();

    void call(@NotNull Event event) throws InvocationTargetException, IllegalAccessException;
}
