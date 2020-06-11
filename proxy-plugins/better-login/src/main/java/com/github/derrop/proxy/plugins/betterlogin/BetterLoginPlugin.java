package com.github.derrop.proxy.plugins.betterlogin;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.derrop.proxy.plugins.betterlogin",
        displayName = "BetterLogin",
        version = 1,
        authors = "derrop",
        dependencies = @Dependency(id = "com.github.derrop.plugins.replay", minimumVersion = 1, optional = true)
)
public class BetterLoginPlugin {

    // TODO add the possibility that users have to type into the chat when logging in

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry, Proxy proxy) {
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new LoginPrepareListener(proxy));
    }

}
