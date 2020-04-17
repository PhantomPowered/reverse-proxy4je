package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;

import java.util.Collection;
import java.util.Map;

public class PlayerData {

    private PlayerInfo playerInfo;
    private GommeGameMode gameMode;
    private Map<String, String> stats;
    private int rank;
    private String clan;
    private Collection<Tag> tags;

    public PlayerData(PlayerInfo playerInfo, GommeGameMode gameMode, Map<String, String> stats, int rank, Collection<Tag> tags) {
        this.playerInfo = playerInfo;
        this.gameMode = gameMode;
        this.stats = stats;
        this.rank = rank;
        this.tags = tags;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
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

    public Collection<Tag> getTags() {
        return this.tags;
    }
}
