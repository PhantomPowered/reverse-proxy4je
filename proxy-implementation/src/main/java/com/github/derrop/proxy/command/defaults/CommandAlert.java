package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.util.ChatColor;

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
