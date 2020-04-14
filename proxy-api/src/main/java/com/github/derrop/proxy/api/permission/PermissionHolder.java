package com.github.derrop.proxy.api.permission;

import org.jetbrains.annotations.NotNull;

public interface PermissionHolder {

    boolean hasPermission(@NotNull String permission);

    void addPermission(@NotNull String permission, boolean set);

    void removePermission(@NotNull String permission);

}
