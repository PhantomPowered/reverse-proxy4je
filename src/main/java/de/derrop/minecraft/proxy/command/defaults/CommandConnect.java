package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.connection.ProxiedPlayer;

public class CommandConnect extends Command {

    public CommandConnect() {
        super("connect");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("This command is only available for players");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage("connect <host>"); // TODO connect [ALL|name] <host>
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        ConnectedProxyClient proxyClient = player.getConnectedClient();
        if (proxyClient == null) {
            sender.sendMessages("You are not connected with any client");
            return;
        }

        NetworkAddress address = NetworkAddress.parse(args[0]);
        if (address == null) {
            sender.sendMessage("§cInvalid address");
            return;
        }

        player.disableAutoReconnect();
        player.useClient(null);

        proxyClient.connect(address).thenAccept(success -> {
            player.enableAutoReconnect();
            if (success) {
                player.useClient(proxyClient);
            }
        });


        /*if (args.length == 0) {
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

        ConnectedProxyClient redirect = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getConnectedClient() : null;

        sender.sendMessage("Connecting the clients to " + address + "...");
        for (ConnectedProxyClient client : clients) {
            CompletableFuture<Boolean> future = client.connect(address);
            if (redirect != null && redirect.getAccountUUID().equals(client.getAccountUUID())) {
                future.thenAccept(success -> {
                    if (success) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                        //((ProxiedPlayer) sender).useClient(redirect);
                    }
                });
            }
        }*/
    }
}
