package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandFind extends NonTabCompleteableCommandCallback {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public CommandFind() {
        super("command.find", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        if (args.length != 2) {
            sender.sendMessage("find blocks <material>");
            sender.sendMessage("find entities <player|all>");
            return CommandResult.BREAK;
        }

        Player player = (Player) sender;

        if (player.getConnectedClient() == null) {
            player.sendMessage("Not connected with any client");
            return CommandResult.BREAK;
        }

        if (args[0].equalsIgnoreCase("blocks")) {
            Material material = Material.matchMaterial(args[1]);
            if (material == null) {
                sender.sendMessage("That material doesn't exist");
                return CommandResult.BREAK;
            }

            this.listBlocks(player, material);
        } else if (args[0].equalsIgnoreCase("entities")) {

            Collection<? extends Entity> entities;

            if (args[1].equalsIgnoreCase("all")) {
                entities = player.getConnectedClient().getWorldDataProvider().getEntitiesInWorld();
            } else if (args[1].equalsIgnoreCase("player")) {
                entities = player.getConnectedClient().getWorldDataProvider().getPlayersInWorld();
            } else {
                sender.sendMessage("Please provide 'player' or 'all' as the type");
                return CommandResult.BREAK;
            }

            player.sendMessage("Entities in your world:");

            for (Entity entity : entities) {
                if (entity instanceof EntityPlayer) {
                    PlayerInfo playerInfo = ((EntityPlayer) entity).getPlayerInfo();
                    if (playerInfo != null) {
                        player.sendMessage(" * " + entity.getLocation().toShortString() + " (" + playerInfo.getUsername() + ")");
                    }
                    continue;
                }
                player.sendMessage(" * " + entity.getLocation().toShortString());
            }

        }

        return CommandResult.END;
    }

    private void listBlocks(Player player, Material material) {
        BlockAccess blockAccess = player.getConnectedClient().getBlockAccess();

        this.findWorker(player, () -> {
            player.sendMessage("Searching for the materials, this may take a while...");

            Collection<Location> locations = blockAccess.getPositions(material);
            if (locations.isEmpty()) {
                player.sendMessage("That material doesn't exist in the loaded chunks");
                return;
            }
            if (locations.size() >= 500) {
                player.sendMessage("Too many positions found: §e" + locations.size());
                return;
            }

            StringBuilder builder = new StringBuilder();
            String splitter = "§e, §7";

            for (Location position : locations) {
                builder.append(splitter).append(position.toShortString());
            }

            player.sendMessage("§aFound the following positions (§e" + locations.size() + "§a): §7" + builder.substring(splitter.length()));
        });
    }

    private void findWorker(Player player, Runnable runnable) {
        player.sendMessage("Waiting for free worker...");

        this.executorService.execute(runnable);
    }

}
