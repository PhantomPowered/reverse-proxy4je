package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;

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
