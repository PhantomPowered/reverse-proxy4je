package com.github.derrop.proxy.plugins.gommecw;

import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.derrop.plugins.gommecw",
        displayName = "GommeCW",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop",
        dependencies = @Dependency(id = "com.github.derrop.plugins.gommestats")
)
public class GommeCWPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer container) {

    }

}
