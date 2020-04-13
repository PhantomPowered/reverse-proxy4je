package com.github.derrop.proxy.player;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.player.OfflinePlayer;

import java.util.UUID;

public class DefaultOfflinePlayer implements OfflinePlayer {

    private final MCProxy proxy;
    private final UUID uniqueId;
    private String name;
    private long lastLogin;

    public DefaultOfflinePlayer(MCProxy proxy, UUID uniqueId, String name, long lastLogin) {
        this.proxy = proxy;
        this.uniqueId = uniqueId;
        this.name = name;
        this.lastLogin = lastLogin;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.proxy.getPermissionProvider().hasPermission(this.uniqueId, permission);
    }

    @Override
    public long getLastLogin() {
        return this.lastLogin;
    }
}
