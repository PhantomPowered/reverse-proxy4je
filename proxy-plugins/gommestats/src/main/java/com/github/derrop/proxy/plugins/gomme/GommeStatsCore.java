package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.stats.StatsProvider;

public class GommeStatsCore {

    private MatchManager matchManager = new MatchManager(this);
    private StatsProvider statsProvider = new StatsProvider();

    public MatchManager getMatchManager() {
        return matchManager;
    }

    public StatsProvider getStatsProvider() {
        return statsProvider;
    }
}
