package com.github.derrop.proxy.service;

import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.service.ServiceRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicServiceRegistryEntry<T> implements ServiceRegistryEntry<T> {

    BasicServiceRegistryEntry(Class<T> service, T provider, PluginContainer pluginContainer, boolean immutable, boolean needsReplacement) {
        this.service = service;
        this.provider = provider;
        this.pluginContainer = pluginContainer;
        this.immutable = immutable;
        this.needsReplacement = needsReplacement;
    }

    private final Class<T> service;

    private final T provider;

    private final PluginContainer pluginContainer;

    private final boolean immutable;

    private final boolean needsReplacement;

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
