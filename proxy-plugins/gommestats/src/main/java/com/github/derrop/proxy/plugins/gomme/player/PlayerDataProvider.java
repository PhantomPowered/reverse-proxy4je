package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.player.clan.ClanInfo;

import java.util.UUID;

// TODO database
public class PlayerDataProvider {

    public PlayerData getData(UUID uniqueId) {
        return null;
    }

    public void updateStatistics(PlayerData data) {

    }

    public long countPlayerData() {
        return -1;
    }

    public long countPlayerData(GommeGameMode gameMode) {
        return -1;
    }

    public ClanInfo getClan(String name) {
        return null;
    }

    public ClanInfo getClanByShortcut(String shortcut) {
        return null;
    }

    public void updateClan(ClanInfo info) {

    }

}
