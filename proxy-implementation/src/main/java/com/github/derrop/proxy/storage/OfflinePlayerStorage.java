package com.github.derrop.proxy.storage;

import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.entity.player.DefaultOfflinePlayer;

import java.util.Collection;
import java.util.UUID;

public class OfflinePlayerStorage extends DatabaseProvidedStorage<DefaultOfflinePlayer> {

    private UUIDNameStorage uuidNameStorage;

    public OfflinePlayerStorage(ServiceRegistry registry) {
        super(registry, "player_storage");
        this.uuidNameStorage = new UUIDNameStorage(registry, "name_uuid_map");
    }

    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        return super.get(uniqueId.toString());
    }

    public void updateOfflinePlayer(OfflinePlayer player) {
        OfflinePlayer oldPlayer = super.get(player.getUniqueId().toString());
        if (oldPlayer != null) {
            this.uuidNameStorage.remove(oldPlayer.getName());
        }

        this.uuidNameStorage.put(player.getName(), player.getUniqueId());

        super.insertOrUpdate(player.getUniqueId().toString(), (DefaultOfflinePlayer) player);
    }

    public Collection<? extends OfflinePlayer> getOfflinePlayers() {
        return super.getAll();
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        UUID uniqueId = this.uuidNameStorage.getUUID(name);
        return uniqueId != null ? this.getOfflinePlayer(uniqueId) : null;
    }

}
