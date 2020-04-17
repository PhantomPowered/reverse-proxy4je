package com.github.derrop.proxy.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface PluginManager {

    @NotNull
    Optional<PluginContainer> fromInstance(@NotNull Object instance);

    @NotNull
    Optional<PluginContainer> getPlugin(@NotNull String id);

    @NotNull
    Collection<PluginContainer> getPlugins();

    boolean isLoaded(@NotNull String id);

    boolean isEnabled(@NotNull String id);

    void detectPlugins();

    void loadPlugins();

    void enablePlugins();

    void disablePlugins();

}
