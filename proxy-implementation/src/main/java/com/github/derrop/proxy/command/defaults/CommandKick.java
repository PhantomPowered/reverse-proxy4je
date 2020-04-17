package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.repository.PlayerRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandKick extends NonTabCompleteableCommandCallback {

    public CommandKick() {
        super("proxy.command.kick", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length < 1) {
            commandSender.sendMessage("kick <name> [message]");
            return CommandResult.BREAK;
        }

        Player player = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PlayerRepository.class).getOnlinePlayer(arguments[0]);
        if (player == null) {
            commandSender.sendMessage("§cThat player isn't online");
            return CommandResult.BREAK;
        }

        String message = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        if (message.trim().isEmpty()) {
            message = "No reason given";
        }

        message = "§7Kicked by §e" + commandSender.getName() + "§7. Reason: " + ChatColor.translateAlternateColorCodes('&', message);
        player.disconnect(TextComponent.fromLegacyText(message));
        return CommandResult.END;
    }
}
