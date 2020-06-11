package com.github.derrop.proxy.plugins.pwarner.listener;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.item.ItemMeta;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningData;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningDatabase;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.TranslatableComponent;
import net.kyori.text.format.TextColor;

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
        String itemName = material + (itemMeta != null && itemMeta.getDisplayName() != null ? (" " + itemMeta.getDisplayName().key()) : "");

        if (data.shouldWarnEquipmentSlot(event.getSlot().getSlotId(), material)) {
            Team team = event.getConnection().getScoreboard().getTeamByEntry(name);

            String color = team == null ? "" : ChatColor.getLastColors(team.getPrefix());
            String fullName = color + name;

            player.sendMessage(String.format("§7%s §7has §6%dx §e%s", fullName, amount, itemName));
        }
    }

}
