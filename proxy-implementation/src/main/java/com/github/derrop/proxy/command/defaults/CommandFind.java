package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.location.BlockPos;
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

        if (args.length != 1) {
            sender.sendMessage("find <material>");
            return CommandResult.BREAK;
        }

        Material material = Material.matchMaterial(args[0]);
        if (material == null) {
            sender.sendMessage("That material doesn't exist");
            return CommandResult.BREAK;
        }

        Player player = (Player) sender;

        if (player.getConnectedClient() == null) {
            player.sendMessage("Not connected with any client");
            return CommandResult.BREAK;
        }

        BlockAccess blockAccess = player.getConnectedClient().getBlockAccess();

        sender.sendMessage("Waiting for free worker...");

        this.executorService.execute(() -> {
            sender.sendMessage("Searching for the materials, this may take a while...");

            Collection<BlockPos> positions = blockAccess.getPositions(material);
            if (positions.isEmpty()) {
                sender.sendMessage("That material doesn't exist in the loaded chunks");
                return;
            }
            if (positions.size() >= 500) {
                sender.sendMessage("Too many positions found: §e" + positions.size());
                return;
            }

            StringBuilder builder = new StringBuilder();
            String splitter = "§e, §7";

            for (BlockPos position : positions) {
                builder.append(splitter).append(position.toShortString());
            }

            sender.sendMessage("§aFound the following positions: §7" + builder.substring(splitter.length()));

        });

        return CommandResult.END;
    }
}
