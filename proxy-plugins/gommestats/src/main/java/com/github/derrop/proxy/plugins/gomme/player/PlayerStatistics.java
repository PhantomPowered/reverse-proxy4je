package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;

import java.util.Map;

public class PlayerStatistics {

    private GommeGameMode gameMode;
    private Map<String, String> stats;
    private int rank;
    private boolean privateStats;

    public PlayerStatistics(GommeGameMode gameMode, Map<String, String> stats, int rank, boolean privateStats) {
        this.gameMode = gameMode;
        this.stats = stats;
        this.rank = rank;
        this.privateStats = privateStats;
    }

    public GommeGameMode getGameMode() {
        return this.gameMode;
    }

    public Map<String, String> getStats() {
        return this.stats;
    }

    public int getRank() {
        return this.rank;
    }

    public boolean isPrivateStats() {
        return this.privateStats;
    }
}
