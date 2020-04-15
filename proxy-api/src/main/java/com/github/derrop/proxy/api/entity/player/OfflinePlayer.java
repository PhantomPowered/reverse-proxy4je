package com.github.derrop.proxy.api.entity.player;

import com.github.derrop.proxy.api.permission.PermissionHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface OfflinePlayer extends PermissionHolder {

    @NotNull UUID getUniqueId();

    @NotNull String getName();

    long getLastLogin();

    int getLastVersion();

}
