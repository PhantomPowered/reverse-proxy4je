package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import com.github.derrop.proxy.entity.permission.DefaultPermissionHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultOfflinePlayer extends DefaultPermissionHolder implements OfflinePlayer {

    public DefaultOfflinePlayer(UUID uniqueID, String name, long lastLogin) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.lastLogin = lastLogin;
    }

    private final UUID uniqueID;

    private final String name;

    private final long lastLogin;

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueID;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public long getLastLogin() {
        return this.lastLogin;
    }
}
