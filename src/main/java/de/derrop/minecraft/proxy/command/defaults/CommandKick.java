package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.ProxiedPlayer;

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

        ProxiedPlayer player = MCProxy.getInstance().getOnlinePlayer(args[0]);

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
