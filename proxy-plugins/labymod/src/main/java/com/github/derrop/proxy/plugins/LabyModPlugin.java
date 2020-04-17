package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.derrop.plugins.labymod",
        displayName = "LabyMod",
        version = 1,
        website = "https://github.com/derrop",
        authors = {"derrop", "derklaro"}
)
public class LabyModPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry) {
        registry.getProviderUnchecked(EventManager.class).registerListener(new LabyModListener());
    }

}
