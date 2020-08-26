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
package com.github.phantompowered.proxy.api.service;

import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.service.exception.ProviderImmutableException;
import com.github.phantompowered.proxy.api.service.exception.ProviderNeedsReplacementException;
import com.github.phantompowered.proxy.api.service.exception.ProviderNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface ServiceRegistry {

    default <T> void setProvider(@Nullable PluginContainer pluginContainer, @NotNull Class<T> service, @NotNull T provider) {
        this.setProvider(pluginContainer, service, provider, false);
    }

    default <T> void setProvider(@Nullable PluginContainer pluginContainer, @NotNull Class<T> service, @NotNull T provider, boolean immutable) {
        this.setProvider(pluginContainer, service, provider, immutable, false);
    }

    <T> void setProvider(@Nullable PluginContainer pluginContainer, @NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement);

    @NotNull <T> Optional<T> getProvider(@NotNull Class<T> service);

    @NotNull <T> Optional<ServiceRegistryEntry<T>> getRegisteredEntry(@NotNull Class<T> service);

    @NotNull <T> T getProviderUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException;

    @NotNull
    Collection<ServiceRegistryEntry<?>> getPluginRegisteredServices(@NotNull PluginContainer pluginContainer);

    default <T> void unregisterService(@NotNull Class<T> service) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException {
        this.unregisterService(service, null);
    }

    <T> void unregisterService(@NotNull Class<T> service, @Nullable T replacement) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException;

    default boolean isRegistered(@NotNull Class<?> service) {
        return this.getProvider(service).isPresent();
    }
}
