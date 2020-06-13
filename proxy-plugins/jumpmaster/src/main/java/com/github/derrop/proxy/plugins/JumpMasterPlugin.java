package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.listener.JumpMasterListener;

@Plugin(
        id = "com.github.derrop.plugins.jumpmaster",
        displayName = "JumpMaster",
        version = 1,
        website = "https://github.com/derrop",
        authors = {"0x80"}
)
public class JumpMasterPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer pluginContainer) {
        registry.getProviderUnchecked(EventManager.class).registerListener(pluginContainer, new JumpMasterListener(registry));

    }

}
