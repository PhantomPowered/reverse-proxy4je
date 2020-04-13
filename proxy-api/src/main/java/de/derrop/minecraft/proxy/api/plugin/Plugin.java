package de.derrop.minecraft.proxy.api.plugin;

import de.derrop.minecraft.proxy.api.Proxy;
import org.jetbrains.annotations.NotNull;

public class Plugin {

    private PluginDescription description;
    private Proxy proxy;

    public void set(@NotNull Proxy proxy, @NotNull PluginDescription description) {
        this.proxy = proxy;
        this.description = description;
    }

    public final Proxy getProxy() {
        return this.proxy;
    }

    public final PluginDescription getDescription() {
        return this.description;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

}
