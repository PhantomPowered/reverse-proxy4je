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
import com.github.phantompowered.proxy.api.event.EventPriority;
import com.github.phantompowered.proxy.api.event.ListenerContainer;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
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

        this.method.setAccessible(true);
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
