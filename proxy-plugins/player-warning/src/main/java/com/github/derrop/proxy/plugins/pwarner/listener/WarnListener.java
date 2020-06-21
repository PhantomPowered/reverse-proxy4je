package com.github.derrop.proxy.plugins.pwarner.listener;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.item.ItemMeta;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningData;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningDatabase;
import com.github.derrop.proxy.plugins.pwarner.storage.WarnedEquipmentSlot;

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
        int amount = event.getItem().getAmount();
        ItemMeta itemMeta = event.getItem().getItemMeta();
        String itemName = material + (itemMeta != null && itemMeta.getDisplayName() != null ? " " + itemMeta.getDisplayName().key() : "");

        WarnedEquipmentSlot slot = data.getEquipmentSlotData(event.getSlot().getSlotId(), material);
        if (slot != null) {
            Team team = event.getConnection().getScoreboard().getTeamByEntry(name);

            String playerColor = team == null ? "" : ChatColor.getLastColors(team.getPrefix());
            ChatColor itemColor = slot.getColor() == null ? ChatColor.YELLOW : slot.getColor();
            String fullName = playerColor + name;

            player.sendMessage(String.format("§7%s §7has %dx %s%s", fullName, amount,itemColor, itemName));
        }
    }

}
