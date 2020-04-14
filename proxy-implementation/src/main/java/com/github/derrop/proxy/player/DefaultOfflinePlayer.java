package com.github.derrop.proxy.player;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    // TODO:

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.proxy.getPermissionProvider().hasPermission(this.uniqueId, permission);
    }

    @Override
    public void addPermission(@NotNull String permission, boolean set) {

    }

    @Override
    public void removePermission(@NotNull String permission) {

    }

    @Override
    public long getLastLogin() {
        return this.lastLogin;
    }
}
