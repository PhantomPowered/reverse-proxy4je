package com.github.derrop.plugins.ruler;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.derrop.plugins.ruler",
        displayName = "Ruler",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class RulerPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer container) {
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new RulerListener());
    }

}
