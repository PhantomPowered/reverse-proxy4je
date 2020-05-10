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

public class CommandReplace extends NonTabCompleteableCommandCallback {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public CommandReplace() {
        super("command.replace", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        if (args.length != 2) {
            sender.sendMessage("replace <sourceMaterial> <targetMaterial>");
            return CommandResult.BREAK;
        }

        Material source = Material.matchMaterial(args[0]);
        Material target = Material.matchMaterial(args[1]);
        if (source == null) {
            sender.sendMessage("The source material doesn't exist");
            return CommandResult.BREAK;
        }
        if (target == null) {
            sender.sendMessage("The target material doesn't exist");
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

            Collection<BlockPos> positions = blockAccess.getPositions(source);
            if (positions.isEmpty()) {
                sender.sendMessage("That material doesn't exist in the loaded chunks");
                return;
            }

            sender.sendMessage("§7Replacing §e" + positions.size() + " §7" + source + " blocks with " + target + "...");

            for (BlockPos position : positions) {
                player.sendBlockChange(position, target);
            }

            sender.sendMessage("§aSuccessfully replaced all §e" + positions.size() + " §apositions");

        });

        return CommandResult.END;
    }
}
