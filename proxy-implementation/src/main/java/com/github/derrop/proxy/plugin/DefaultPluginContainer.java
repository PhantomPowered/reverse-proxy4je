package com.github.derrop.proxy.plugin;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class DefaultPluginContainer implements PluginContainer {

    DefaultPluginContainer(Plugin plugin, Class<?> mainClass, URLClassLoader urlClassLoader, Path path) {
        this.plugin = plugin;
        this.mainClass = mainClass;
        this.urlClassLoader = urlClassLoader;
        this.path = path;
        this.pluginState = PluginState.LOADED;
    }

    private final Plugin plugin;

    private final Class<?> mainClass;

    private final URLClassLoader urlClassLoader;

    private final Path path;

    private PluginState pluginState;

    @Override
    public @NotNull String getId() {
        return this.plugin.id();
    }

    @Override
    public @Nullable String getDisplayName() {
        return this.plugin.displayName().trim().isEmpty() ? null : this.plugin.displayName();
    }

    @Override
    public int getVersion() {
        return this.plugin.version();
    }

    @Override
    public @NotNull String getWebSite() {
        return this.plugin.website();
    }

    @Override
    public @NotNull String[] getAuthors() {
        return this.plugin.authors();
    }

    @Override
    public @Nullable String getDescription() {
        return this.plugin.description().trim().isEmpty() ? null : this.plugin.description();
    }

    @Override
    public @NotNull Dependency[] getDependencies() {
        return this.plugin.dependencies();
        /*Üreturn Arrays.stream(this.plugin.dependencies()).map(e -> {
            Optional<PluginContainer> container = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PluginManager.class).getPlugin(e.id());
            return container.orElse(null);
        }).filter(Objects::nonNull).toArray(Dependency[]::new);*/
    }

    @Override
    public @NotNull PluginState getState() {
        return this.pluginState;
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return MCProxy.getInstance().getServiceRegistry();
    }

    @Override
    public @NotNull URLClassLoader getClassLoader() {
        return this.urlClassLoader;
    }

    @Override
    public @NotNull Path getPluginPath() {
        return this.path;
    }

    @Override
    public @NotNull Class<?> getMainClass() {
        return this.mainClass;
    }

    @Override
    public void setPluginState(@NotNull PluginState pluginState) {
        this.pluginState = pluginState;
    }
}
