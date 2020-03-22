package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;

public class CommandList extends Command {

    public CommandList() {
        super("list");
        super.setPermission("command.list");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        sender.sendMessage("Connected clients:");
        for (ConnectedProxyClient onlineClient : MCProxy.getInstance().getOnlineClients()) {
            sender.sendMessage("- §e" + onlineClient.getAccountName() + " §7(" + (onlineClient.getRedirector() != null ? "§cnot free" : "§afree") + "§7); Connected on: " + onlineClient.getAddress());
        }
    }
}
