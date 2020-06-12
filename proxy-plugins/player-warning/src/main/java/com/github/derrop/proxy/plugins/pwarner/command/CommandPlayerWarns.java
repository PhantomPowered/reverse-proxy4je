package com.github.derrop.proxy.plugins.pwarner.command;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningData;
import com.github.derrop.proxy.plugins.pwarner.storage.PlayerWarningDatabase;
import com.github.derrop.proxy.plugins.pwarner.storage.WarnedEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class CommandPlayerWarns extends NonTabCompleteableCommandCallback {

    private PlayerWarningDatabase database;

    public CommandPlayerWarns(PlayerWarningDatabase database) {
        super("command.playerwarns", null);
        this.database = database;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        Player player = (Player) sender;

        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {

            PlayerWarningData data = this.database.getData(player.getUniqueId());
            if (data == null) {
                sender.sendMessage("No data found");
                return CommandResult.BREAK;
            }

            switch (args[1].toLowerCase()) {
                case "equip": {
                    if (data.getEquipmentSlots().isEmpty()) {
                        sender.sendMessage("No warnings on equipment slot updates defined");
                        return CommandResult.BREAK;
                    }

                    for (Map.Entry<Integer, Collection<WarnedEquipmentSlot>> entry : data.getEquipmentSlots().entrySet()) {
                        if (entry.getValue().isEmpty()) {
                            continue;
                        }

                        EquipmentSlot slot = EquipmentSlot.getById(entry.getKey());
                        if (slot == null) {
                            continue;
                        }

                        sender.sendMessage("Warnings for the slot §e" + slot.getFormattedName());
                        for (WarnedEquipmentSlot warn : entry.getValue()) {
                            sender.sendMessage(" - §e" + (warn.getColor() == null ? ChatColor.YELLOW : warn.getColor()) + warn.getMaterial());
                        }
                    }
                }
                break;
            }

        } else if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("equip")) {
            EquipmentSlot slot;
            try {
                slot = EquipmentSlot.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException ex) {
                sender.sendMessage("That slot doesn't exist, available slots: §7" + Arrays.toString(EquipmentSlot.values()));
                return CommandResult.BREAK;
            }

            Material material = Material.matchMaterial(args[2]);
            if (material == null) {
                sender.sendMessage("That material doesn't exist");
                return CommandResult.BREAK;
            }

            ChatColor color = args.length == 4 ? ChatColor.getByChar(args[3].charAt(args[3].length() - 1)) : null;

            PlayerWarningData data = this.database.getOrCreate(player.getUniqueId());
            if (data.addEquipmentSlot(new WarnedEquipmentSlot(slot, material, color))) {
                sender.sendMessage("You will be always warned when a player has §e"
                        + (color != null ? color : "") + material
                        + " §7in their §e" + slot.getFormattedName());
            } else {
                sender.sendMessage("You will no more be warned when a player has §e"
                        + (color != null ? color : "") + material
                        + " §7in their §e" + slot.getFormattedName());
            }

            this.database.update(data);

            return CommandResult.SUCCESS;
        } else {
            sender.sendMessage("playerwarns equip <" + Arrays.toString(EquipmentSlot.values()) + "> <Material> [Color]");
            sender.sendMessage("playerwarns list equip");
        }

        return CommandResult.END;
    }
}
