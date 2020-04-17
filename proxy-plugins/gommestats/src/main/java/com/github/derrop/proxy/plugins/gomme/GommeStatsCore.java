package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.player.PlayerDataProvider;

public class GommeStatsCore {

    private final MatchManager matchManager = new MatchManager(this);
    private final PlayerDataProvider playerDataProvider = new PlayerDataProvider();

    private final ServiceRegistry registry;

    public GommeStatsCore(ServiceRegistry registry) {
        this.registry = registry;
    }

    public MatchManager getMatchManager() {
        return this.matchManager;
    }

    public PlayerDataProvider getPlayerDataProvider() {
        return this.playerDataProvider;
    }

    public ServiceRegistry getRegistry() {
        return this.registry;
    }
}
