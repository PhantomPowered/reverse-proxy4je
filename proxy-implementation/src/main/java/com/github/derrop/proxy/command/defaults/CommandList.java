package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;

import java.util.Collection;

public class CommandList extends Command {

    public CommandList() {
        super("list");
        super.setPermission("command.list");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        Collection<? extends ServiceConnection> clients = MCProxy.getInstance().getOnlineClients();
        sender.sendMessage("Connected clients: (" + clients.size() + ")");
        for (ServiceConnection onlineClient : clients) {
            sender.sendMessage("- §e" + onlineClient.getName() + " §7(" + (onlineClient.getPlayer() != null ? "§cnot free" : "§afree") + "§7); Connected on: " + onlineClient.getServerAddress());
        }
        sender.sendMessage(" ");

        Collection<ProxiedPlayer> players = MCProxy.getInstance().getPlayerRepository().getOnlinePlayers();
        sender.sendMessage("Connected users: (" + players.size() + ")");
        for (ProxiedPlayer player : players) {
            sender.sendMessage("- §e" + player.getName() + " §7(on: " + (player.getConnectedClient() == null ? "§cnone" : "§7" + player.getConnectedClient().getName()) + "§7)");
        }
    }
}
