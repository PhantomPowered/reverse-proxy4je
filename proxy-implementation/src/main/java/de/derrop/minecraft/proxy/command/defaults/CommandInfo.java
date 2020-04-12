package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;

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
