package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;

public class LabyModPluginContainer extends PluginContainer {

    @Override
    public void onEnable() {
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new LabyModListener());
    }

    @Override
    public void onDisable() {
    }
}
