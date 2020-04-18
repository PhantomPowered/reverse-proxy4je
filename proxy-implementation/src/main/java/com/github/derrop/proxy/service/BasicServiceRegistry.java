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
package com.github.derrop.proxy.service;

import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.service.ServiceRegistryEntry;
import com.github.derrop.proxy.api.service.exception.ProviderImmutableException;
import com.github.derrop.proxy.api.service.exception.ProviderNeedsReplacementException;
import com.github.derrop.proxy.api.service.exception.ProviderNotRegisteredException;
import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public final class BasicServiceRegistry implements ServiceRegistry {

    private final ConcurrentMap<Class<?>, ServiceRegistryEntry<?>> entries = new MapMaker()
            .concurrencyLevel(3)
            .makeMap();

    @Override
    public <T> void setProvider(@Nullable PluginContainer pluginContainer, @NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement) {
        this.entries.put(service, new BasicServiceRegistryEntry<>(service, provider, pluginContainer, immutable, needsReplacement));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> getProvider(@NotNull Class<T> service) {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        return entry == null ? Optional.empty() : Optional.of(entry.getProvider());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<ServiceRegistryEntry<T>> getRegisteredEntry(@NotNull Class<T> service) {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        return Optional.ofNullable(entry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @NotNull T getProviderUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        if (entry == null) {
            throw new ProviderNotRegisteredException(service);
        }

        return entry.getProvider();
    }

    @Override
    public @NotNull Collection<ServiceRegistryEntry<?>> getPluginRegisteredServices(@NotNull PluginContainer pluginContainer) {
        Collection<ServiceRegistryEntry<?>> out = new ArrayList<>();
        for (ServiceRegistryEntry<?> value : this.entries.values()) {
            if (value.getPluginContainer() != null && value.getPluginContainer().equals(pluginContainer)) {
                out.add(value);
            }
        }

        return out;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void unregisterService(@NotNull Class<T> service, @Nullable T replacement) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        if (entry == null) {
            throw new ProviderNotRegisteredException(service);
        }

        if (entry.isImmutable()) {
            throw new ProviderImmutableException(service);
        }

        if (entry.needsReplacement() && replacement == null) {
            throw new ProviderNeedsReplacementException(service);
        }

        this.entries.remove(service);
    }
}
