package com.github.derrop.proxy.api.service;

import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServiceRegistryEntry<T> {

    @NotNull
    Class<T> getService();

    @NotNull
    T getProvider();

    @Nullable
    Plugin getPlugin();

    boolean isImmutable();

}
