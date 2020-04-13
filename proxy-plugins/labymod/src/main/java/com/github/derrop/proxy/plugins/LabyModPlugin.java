package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.plugin.Plugin;

public class LabyModPlugin extends Plugin {

    @Override
    public void onEnable() {
        super.getServiceRegistry().getProviderUnchecked(Proxy.class).getEventManager().registerListener(new LabyModListener());
    }

    @Override
    public void onDisable() {
    }
}
