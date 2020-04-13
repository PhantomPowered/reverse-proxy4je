package com.github.derrop.proxy.permission;

import java.util.Collection;
import java.util.UUID;

public class PermissionEntity {

    private UUID uniqueId;
    private Collection<String> permissions;

    public PermissionEntity(UUID uniqueId, Collection<String> permissions) {
        this.uniqueId = uniqueId;
        this.permissions = permissions;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }
}
