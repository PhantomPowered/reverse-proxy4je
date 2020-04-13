package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;

public class CommandInfo extends Command {

    public CommandInfo() {
        super("info", "i");
        super.setPermission("command.info");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ServiceConnection client = ((ProxiedPlayer) sender).getConnectedClient();
            sender.sendMessage("§7Connected with client: " + (client == null ? "§cNONE" : "§e" + client.getName() + " §7on §e" + client.getServerAddress()));
        }
        sender.sendMessage("§7Connected clients: §e" + MCProxy.getInstance().getOnlineClients().size() + " §7(Free: §a" + MCProxy.getInstance().getFreeClients().size() + "§7)");
    }
}
