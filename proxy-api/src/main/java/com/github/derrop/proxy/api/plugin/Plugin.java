package com.github.derrop.proxy.api.plugin;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

public class Plugin {

    private PluginDescription description;
    private ServiceRegistry serviceRegistry;

    public void set(@NotNull PluginDescription description, @NotNull ServiceRegistry serviceRegistry) {
        this.description = description;
        this.serviceRegistry = serviceRegistry;
    }

    public final PluginDescription getDescription() {
        return this.description;
    }

    public final ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

}
