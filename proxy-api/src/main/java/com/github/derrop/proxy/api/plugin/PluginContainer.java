package com.github.derrop.proxy.api.plugin;

import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLClassLoader;
import java.nio.file.Path;

public interface PluginContainer {

    @NotNull String getId();

    @Nullable String getDisplayName();

    int getVersion();

    @NotNull String getWebSite();

    @NotNull String[] getAuthors();

    @NotNull Dependency[] getDependencies();

    @NotNull PluginState getState();

    @NotNull ServiceRegistry getServiceRegistry();

    @NotNull URLClassLoader getClassLoader();

    @NotNull Path getPluginPath();

    @NotNull Class<?> getMainClass();

    void setPluginState(@NotNull PluginState pluginState);
}
