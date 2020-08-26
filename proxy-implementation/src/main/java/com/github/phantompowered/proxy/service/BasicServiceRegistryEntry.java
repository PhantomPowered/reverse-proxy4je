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
package com.github.phantompowered.proxy.service;

import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.service.ServiceRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicServiceRegistryEntry<T> implements ServiceRegistryEntry<T> {

    private final Class<T> service;
    private final T provider;
    private final PluginContainer pluginContainer;
    private final boolean immutable;
    private final boolean needsReplacement;

    BasicServiceRegistryEntry(Class<T> service, T provider, PluginContainer pluginContainer, boolean immutable, boolean needsReplacement) {
        this.service = service;
        this.provider = provider;
        this.pluginContainer = pluginContainer;
        this.immutable = immutable;
        this.needsReplacement = needsReplacement;
    }

    @Override
    public @NotNull Class<T> getService() {
        return this.service;
    }

    @NotNull
    @Override
    public T getProvider() {
        return this.provider;
    }

    @Override
    public @Nullable PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    @Override
    public boolean isImmutable() {
        return this.immutable;
    }

    @Override
    public boolean needsReplacement() {
        return this.needsReplacement;
    }
}
