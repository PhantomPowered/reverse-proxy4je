package com.github.derrop.proxy.service;

import com.github.derrop.proxy.api.plugin.Plugin;
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
    public <T> void setProvider(@Nullable Plugin plugin, @NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement) {
        this.entries.put(service, new BasicServiceRegistryEntry<>(service, provider, plugin, immutable, needsReplacement));
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
    public @NotNull Collection<ServiceRegistryEntry<?>> getPluginRegisteredServices(@NotNull Plugin plugin) {
        Collection<ServiceRegistryEntry<?>> out = new ArrayList<>();
        for (ServiceRegistryEntry<?> value : this.entries.values()) {
            if (value.getPlugin() != null && value.getPlugin().equals(plugin)) {
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
