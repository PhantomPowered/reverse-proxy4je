package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.util.ChatColor;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;

import java.util.Arrays;

public class CommandKick extends Command {

    public CommandKick() {
        super("kick");
        super.setPermission("command.kick");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("kick <name> [message]");
            return;
        }

        ProxiedPlayer player = MCProxy.getInstance().getPlayerRepository().getOnlinePlayer(args[0]);

        if (player == null) {
            sender.sendMessage("§cThat player isn't online");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (message.trim().isEmpty()) {
            message = "No reason given";
        }

        message = "§7Kicked by §e" + sender.getName() + "§7. Reason: " + ChatColor.translateAlternateColorCodes('&', message);
        player.disconnect(TextComponent.fromLegacyText(message));
    }
}
