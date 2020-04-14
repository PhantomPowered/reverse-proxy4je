package com.github.derrop.proxy.api.service;

import com.github.derrop.proxy.api.plugin.Plugin;
import com.github.derrop.proxy.api.service.exception.ProviderImmutableException;
import com.github.derrop.proxy.api.service.exception.ProviderNeedsReplacementException;
import com.github.derrop.proxy.api.service.exception.ProviderNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface ServiceRegistry {

    default <T> void setProvider(@Nullable Plugin plugin, @NotNull Class<T> service, @NotNull T provider) {
        this.setProvider(plugin, service, provider, false);
    }

    default <T> void setProvider(@Nullable Plugin plugin, @NotNull Class<T> service, @NotNull T provider, boolean immutable) {
        this.setProvider(plugin, service, provider, immutable, false);
    }

    <T> void setProvider(@Nullable Plugin plugin, @NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement);

    @NotNull <T> Optional<T> getProvider(@NotNull Class<T> service);

    @NotNull <T> Optional<ServiceRegistryEntry<T>> getRegisteredEntry(@NotNull Class<T> service);

    @NotNull <T> T getProviderUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException;

    @NotNull
    Collection<ServiceRegistryEntry<?>> getPluginRegisteredServices(@NotNull Plugin plugin);

    default <T> void unregisterService(@NotNull Class<T> service) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException {
        this.unregisterService(service, null);
    }

    <T> void unregisterService(@NotNull Class<T> service, @Nullable T replacement) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException;

    default boolean isRegistered(@NotNull Class<?> service) {
        return this.getProvider(service).isPresent();
    }
}
