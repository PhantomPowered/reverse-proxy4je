package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import com.github.derrop.proxy.entity.permission.DefaultPermissionHolder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class DefaultOfflinePlayer extends DefaultPermissionHolder implements OfflinePlayer, Serializable {

    public DefaultOfflinePlayer(UUID uniqueID, String name, long lastLogin, int lastVersion) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.lastLogin = lastLogin;
        this.lastVersion = lastVersion;
    }

    private final UUID uniqueID;
    private final String name;
    private final long lastLogin;
    private final int lastVersion;

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

    @Override
    public int getLastVersion() {
        return this.lastVersion;
    }
}
