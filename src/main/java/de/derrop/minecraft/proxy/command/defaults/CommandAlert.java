package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import net.md_5.bungee.api.ChatColor;

public class CommandAlert extends Command {

    public CommandAlert() {
        super("alert");
        super.setPermission("command.alert");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        String message = "§cALERT §8| §7" + ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

        for (ConnectedProxyClient onlineClient : MCProxy.getInstance().getOnlineClients()) {
            if (onlineClient.getRedirector() != null) {
                onlineClient.getRedirector().sendMessage("");
                onlineClient.getRedirector().sendMessage(message);
                onlineClient.getRedirector().sendMessage("");
            }
        }
    }
}
