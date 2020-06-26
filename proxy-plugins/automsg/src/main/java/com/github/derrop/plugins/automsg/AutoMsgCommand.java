package com.github.derrop.plugins.automsg;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.player.Player;
import org.jetbrains.annotations.NotNull;

public class AutoMsgCommand extends NonTabCompleteableCommandCallback {

    private final AutoMsgDatabase database;

    public AutoMsgCommand(AutoMsgDatabase database) {
        super("command.automsg", null);
        this.database = database;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        String message = String.join(" ", args).trim();
        if (message.isEmpty()) {
            sender.sendMessage("Please provide an AFK message");
            sender.sendMessage("You can use the following placeholders:");
            sender.sendMessage("§e%date% §7- the exact time of your last disconnection in a readable format");
            sender.sendMessage("§e%min% §7- the exact minutes since your last disconnection with 2 decimal numbers");
            return CommandResult.BREAK;
        }

        this.database.updateMessage(sender.getUniqueId(), message);
        sender.sendMessage("§aSuccessfully changed the AFK message to §e" + message);

        return CommandResult.END;
    }
}
