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
package com.github.phantompowered.proxy.plugin;

import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Dependency;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLClassLoader;
import java.nio.file.Path;

public final class DefaultPluginContainer implements PluginContainer {

    private final Plugin plugin;
    private final ServiceRegistry registry;
    private final Class<?> mainClass;
    private final URLClassLoader urlClassLoader;
    private final Path path;
    private final Path dataFolder;
    private PluginState pluginState;

    DefaultPluginContainer(Path pluginsDirectory, Plugin plugin, ServiceRegistry registry, Class<?> mainClass, URLClassLoader urlClassLoader, Path path) {
        this.plugin = plugin;
        this.registry = registry;
        this.mainClass = mainClass;
        this.urlClassLoader = urlClassLoader;
        this.path = path;
        this.dataFolder = pluginsDirectory.resolve(plugin.displayName());
        this.pluginState = PluginState.LOADED;
    }

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
    }

    @Override
    public @NotNull PluginState getState() {
        return this.pluginState;
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.registry;
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
    public @NotNull Path getDataFolder() {
        return this.dataFolder;
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
