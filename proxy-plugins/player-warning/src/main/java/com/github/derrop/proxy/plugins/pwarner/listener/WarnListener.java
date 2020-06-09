package com.github.derrop.proxy.plugins.pwarner.listener;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningData;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningDatabase;

public class WarnListener {

    private PlayerWarningDatabase database;

    public WarnListener(PlayerWarningDatabase database) {
        this.database = database;
    }

    @Listener
    public void handleEquipmentSlotChange(EquipmentSlotChangeEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer entity = (EntityPlayer) event.getEntity();
        Player player = event.getConnection().getPlayer();
        if (player == null) {
            return;
        }

        PlayerWarningData data = this.database.getData(player.getUniqueId());
        if (data == null) {
            return;
        }

        PlayerInfo playerInfo = entity.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }

        String name = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName() : playerInfo.getUsername();
        Material material = Material.getMaterial(event.getItem().getItemId());

        if (data.shouldWarnEquipmentSlot(event.getSlot().getSlotId(), material)) {
            player.sendMessage("§eEquipment §8| §7The player §e" + name + " §7has §e" + material + " (" + event.getItem().getAmount() + ") §7in their §e" + event.getSlot().getFormattedName());
        }
    }

}
