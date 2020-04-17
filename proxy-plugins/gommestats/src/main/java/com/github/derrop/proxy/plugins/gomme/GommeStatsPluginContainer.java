package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.Plugin;
import com.github.derrop.proxy.plugins.gomme.secret.GommeSpectatorDetector;

public class GommeStatsPlugin extends Plugin {

    @Override
    public void onEnable() {
        GommeStatsCore core = new GommeStatsCore();
        //super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new TeamParser(core));
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new GommeMatchListener(core.getMatchManager()));
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new GommeSpectatorDetector(core));
    }

    @Override
    public void onDisable() {
        
    }
}
