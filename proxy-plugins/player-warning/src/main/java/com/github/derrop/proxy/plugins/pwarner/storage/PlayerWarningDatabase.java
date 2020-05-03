package com.github.derrop.proxy.plugins.pwarner.storage;

import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;

import java.util.UUID;

public class PlayerWarningDatabase extends DatabaseProvidedStorage<PlayerWarningData> {
    public PlayerWarningDatabase(ServiceRegistry registry) {
        super(registry, "plugin_player_warning_data", PlayerWarningData.class);
    }

    public PlayerWarningData getData(UUID uniqueId) {
        return super.get(uniqueId.toString());
    }

    public PlayerWarningData getOrCreate(UUID uniqueId) {
        PlayerWarningData data = this.getData(uniqueId);
        if (data == null) {
            data = PlayerWarningData.newData(uniqueId);
            super.insert(uniqueId.toString(), data);
        }
        return data;
    }

    public void update(PlayerWarningData data) {
        super.update(data.getTargetPlayerId().toString(), data);
    }

}
