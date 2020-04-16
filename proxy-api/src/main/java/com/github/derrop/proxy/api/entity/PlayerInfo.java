package com.github.derrop.proxy.api.entity;

import com.github.derrop.proxy.api.entity.player.GameMode;

import java.util.UUID;

public interface PlayerInfo {

    UUID getUniqueId();

    String getUsername();

    String[][] getProperties();

    GameMode getGamemode();

    int getPing();

    String getDisplayName();

}
