package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.api.util.ChatColor;

public class CommandAlert extends Command {

    public CommandAlert() {
        super("alert");
        super.setPermission("command.alert");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        String message = "§cALERT §8| §7" + ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

        for (ServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
            if (onlineClient.getPlayer() != null) {
                onlineClient.getPlayer().sendMessage("");
                onlineClient.getPlayer().sendMessage(message);
                onlineClient.getPlayer().sendMessage("");
            }
        }
    }
}
