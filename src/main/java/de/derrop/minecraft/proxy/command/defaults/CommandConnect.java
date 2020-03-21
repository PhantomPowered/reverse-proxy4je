package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.util.NetworkAddress;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandConnect extends Command {

    public CommandConnect() {
        super("connect");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("connect <ALL|name> <host>");
            return;
        }

        NetworkAddress address = NetworkAddress.parse(args[1]);
        if (address == null) {
            sender.sendMessage("§cInvalid address");
            return;
        }

        Collection<ConnectedProxyClient> clients = args[0].equalsIgnoreCase("all") ?
                MCProxy.getInstance().getOnlineClients() :
                MCProxy.getInstance().getOnlineClients().stream().filter(proxyClient -> proxyClient.getAccountName().equalsIgnoreCase(args[0])).collect(Collectors.toList());

        if (clients.isEmpty()) {
            sender.sendMessage("§cNo client matching the given name found");
            return;
        }

        sender.sendMessage("Connecting the clients to " + address + "...");
        for (ConnectedProxyClient client : clients) {
            client.connect(address);
        }
    }
}
