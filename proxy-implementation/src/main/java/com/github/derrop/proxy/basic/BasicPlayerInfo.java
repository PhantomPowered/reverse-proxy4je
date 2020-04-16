package com.github.derrop.proxy.basic;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.entity.player.GameMode;

import java.util.UUID;

public class BasicPlayerInfo implements PlayerInfo {

    private UUID uniqueId;

    private String username;
    private String[][] properties;

    private GameMode gamemode;

    private int ping;

    private String displayName;

    public BasicPlayerInfo(UUID uniqueId, String username, String[][] properties, GameMode gamemode, int ping, String displayName) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.properties = properties;
        this.gamemode = gamemode;
        this.ping = ping;
        this.displayName = displayName;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getUsername() {
        return this.username;
    }

    public String[][] getProperties() {
        return this.properties;
    }

    public GameMode getGamemode() {
        return this.gamemode;
    }

    public int getPing() {
        return this.ping;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProperties(String[][] properties) {
        this.properties = properties;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
