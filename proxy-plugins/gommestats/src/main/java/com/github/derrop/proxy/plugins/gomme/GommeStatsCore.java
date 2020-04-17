package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.player.PlayerDataProvider;

public class GommeStatsCore {

    private MatchManager matchManager = new MatchManager(this);
    private PlayerDataProvider playerDataProvider = new PlayerDataProvider();

    public MatchManager getMatchManager() {
        return matchManager;
    }

    public PlayerDataProvider getPlayerDataProvider() {
        return playerDataProvider;
    }
}
