package com.github.derrop.proxy.storage;

import com.github.derrop.proxy.api.service.ServiceRegistry;

import java.util.UUID;

class UUIDNameStorage extends DatabaseProvidedStorage<UUID> {

    public UUIDNameStorage(ServiceRegistry registry, String table) {
        super(registry, table);
    }

    public void remove(String name) {
        super.delete(name);
    }

    public void put(String name, UUID uuid) {
        super.insert(name, uuid);
    }

    public UUID getUUID(String name) {
        return super.get(name);
    }

}
