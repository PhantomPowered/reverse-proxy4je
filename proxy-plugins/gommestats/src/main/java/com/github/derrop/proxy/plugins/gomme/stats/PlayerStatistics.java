package com.github.derrop.proxy.plugins.gomme.stats;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;

import java.util.Map;

public class PlayerStatistics {

    private PlayerInfo playerInfo;
    private GommeGameMode gameMode;
    private Map<String, String> stats;
    private int rank;

    public PlayerStatistics(PlayerInfo playerInfo, GommeGameMode gameMode, Map<String, String> stats, int rank) {
        this.playerInfo = playerInfo;
        this.gameMode = gameMode;
        this.stats = stats;
        this.rank = rank;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public GommeGameMode getGameMode() {
        return gameMode;
    }

    public Map<String, String> getStats() {
        return stats;
    }

    public int getRank() {
        return rank;
    }
}
