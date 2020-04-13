package com.github.derrop.proxy.api.plugin;

import java.nio.file.Path;
import java.util.Collection;

public interface PluginManager {

    void loadPlugins(Path directory);

    void enablePlugins();

    void disablePlugins();

    Plugin getEnabledPlugin(String name);

    Collection<Plugin> getEnabledPlugins();

    boolean isPluginLoaded(String name);

    boolean isPluginEnabled(String name);

}
