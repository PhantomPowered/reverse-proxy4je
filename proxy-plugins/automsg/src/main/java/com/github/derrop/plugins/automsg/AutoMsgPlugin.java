package com.github.derrop.plugins.automsg;

import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.derrop.plugins.automsg",
        displayName = "AutoMsg",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class AutoMsgPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        AutoMsgDatabase database = new AutoMsgDatabase(registry);

        registry.getProviderUnchecked(EventManager.class).registerListener(container, new AutoMsgListener(database));
        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new AutoMsgCommand(database), "automsg");
    }

}
