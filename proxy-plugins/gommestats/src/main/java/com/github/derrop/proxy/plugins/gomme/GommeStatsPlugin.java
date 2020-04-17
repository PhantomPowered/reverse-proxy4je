package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.secret.GommeSpectatorDetector;

@Plugin(
        id = "com.github.derrop.plugins.gommestats",
        displayName = "GommeStats",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class GommeStatsPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry) {
        GommeStatsCore core = new GommeStatsCore();

        //super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new TeamParser(core));
        registry.getProviderUnchecked(EventManager.class).registerListener(new GommeMatchListener(core.getMatchManager()));
        registry.getProviderUnchecked(EventManager.class).registerListener(new GommeSpectatorDetector(core));
    }
}
