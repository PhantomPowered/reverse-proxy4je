package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;

import java.util.Collection;
import java.util.Map;

public class PlayerData {

    private PlayerInfo playerInfo;
    private Map<GommeGameMode, PlayerStatistics> statistics;
    private String clan;
    private Collection<Tag> tags;

    public PlayerData(PlayerInfo playerInfo, Map<GommeGameMode, PlayerStatistics> statistics, String clan, Collection<Tag> tags) {
        this.playerInfo = playerInfo;
        this.statistics = statistics;
        this.clan = clan;
        this.tags = tags;
    }

    public PlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }

    public Map<GommeGameMode, PlayerStatistics> getStatistics() {
        return this.statistics;
    }

    public String getClan() {
        return this.clan;
    }

    public Collection<Tag> getTags() {
        return this.tags;
    }
}
