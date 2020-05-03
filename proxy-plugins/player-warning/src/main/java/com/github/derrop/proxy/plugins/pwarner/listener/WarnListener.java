package com.github.derrop.proxy.plugins.pwarner.listener;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.PlayerEquipmentSlotChangeEvent;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningData;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningDatabase;

public class WarnListener {

    private PlayerWarningDatabase database;

    public WarnListener(PlayerWarningDatabase database) {
        this.database = database;
    }

    @Listener
    public void handleEquipmentSlotChange(PlayerEquipmentSlotChangeEvent event) {
        Player player = event.getConnection().getPlayer();
        if (player == null) {
            return;
        }

        PlayerWarningData data = this.database.getData(player.getUniqueId());
        if (data == null) {
            return;
        }

        String name = event.getPlayerInfo().getDisplayName() != null ? event.getPlayerInfo().getDisplayName() : event.getPlayerInfo().getUsername();
        Material material = Material.getMaterial(event.getItem().getItemId());

        if (data.shouldWarnEquipmentSlot(event.getSlot().getSlotId(), material)) {
            player.sendMessage("§eEquipment §8| §7The player §e" + name + " §7has §e" + material + " §7in the §e" + event.getSlot().getFormattedName());
        }
    }

}
