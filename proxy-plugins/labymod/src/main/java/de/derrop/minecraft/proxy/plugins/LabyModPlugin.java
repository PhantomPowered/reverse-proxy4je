package de.derrop.minecraft.proxy.plugins;

import de.derrop.minecraft.proxy.api.plugin.Plugin;

public class LabyModPlugin extends Plugin {

    @Override
    public void onEnable() {
        super.getProxy().getEventManager().registerListener(new LabyModListener());
    }

    @Override
    public void onDisable() {
    }
}
