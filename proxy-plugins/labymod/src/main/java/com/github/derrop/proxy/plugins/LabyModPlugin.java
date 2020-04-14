package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.Plugin;

public class LabyModPlugin extends Plugin {

    @Override
    public void onEnable() {
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new LabyModListener());
    }

    @Override
    public void onDisable() {
    }
}
