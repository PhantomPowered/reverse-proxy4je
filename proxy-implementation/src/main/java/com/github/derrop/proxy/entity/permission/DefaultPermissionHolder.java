package com.github.derrop.proxy.entity.permission;

import com.github.derrop.proxy.api.entity.permission.PermissionHolder;
import org.jetbrains.annotations.NotNull;

// TODO
public class DefaultPermissionHolder implements PermissionHolder {

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    @Override
    public void addPermission(@NotNull String permission, boolean set) {

    }

    @Override
    public void removePermission(@NotNull String permission) {

    }
}
