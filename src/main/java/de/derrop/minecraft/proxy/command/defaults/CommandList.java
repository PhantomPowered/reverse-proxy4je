package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import net.md_5.bungee.connection.ProxiedPlayer;

import java.util.Collection;

public class CommandList extends Command {

    public CommandList() {
        super("list");
        super.setPermission("command.list");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        Collection<ConnectedProxyClient> clients = MCProxy.getInstance().getOnlineClients();
        sender.sendMessage("Connected clients: ("+clients.size()+")");
        for (ConnectedProxyClient onlineClient : clients) {
            sender.sendMessage("- §e" + onlineClient.getAccountName() + " §7(" + (onlineClient.getRedirector() != null ? "§cnot free" : "§afree") + "§7); Connected on: " + onlineClient.getAddress());
        }
        sender.sendMessage(" ");

        Collection<ProxiedPlayer> players = MCProxy.getInstance().getOnlinePlayers();
        sender.sendMessage("Connected users: (" + players.size() + ")");
        for (ProxiedPlayer player : players) {
            sender.sendMessage("- §e" + player.getName() + " §7(on: " + (player.getConnectedClient() == null ? "§cnone" : "§7" + player.getConnectedClient().getAccountName()) + "§7)");
        }
    }
}
