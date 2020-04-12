package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.api.BungeeTitle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommandConnect extends Command {

    public CommandConnect() {
        super("connect");
        super.setPermission("command.connect");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("connect [ALL|name] <host>");
            return;
        }

        if (args.length != 2 && !(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("This command is only available for players");
            return;
        }

        if (args.length == 1) {
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

            this.connect(proxyClient, address);
        } else {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return;
            }

            Collection<ConnectedProxyClient> clients = MCProxy.getInstance().getOnlineClients().stream()
                    .filter(proxyClient -> args[0].equalsIgnoreCase("all") || proxyClient.getAccountName().equalsIgnoreCase(args[0]))
                    .collect(Collectors.toList());

            if (clients.isEmpty()) {
                sender.sendMessage("§cNo client matching found");
                return;
            }

            CountDownLatch countDownLatch = new CountDownLatch(clients.size());

            for (ConnectedProxyClient client : clients) {
                Constants.EXECUTOR_SERVICE.execute(() -> {
                    try {
                        this.connect(client, address).get(10, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                        exception.printStackTrace();
                    }
                    countDownLatch.countDown();
                });
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            sender.sendMessage("§aDone");
        }
    }

    private CompletableFuture<Void> connect(ConnectedProxyClient proxyClient, NetworkAddress address) {
        if (proxyClient.getAddress().equals(address)) {
            return CompletableFuture.completedFuture(null);
        }

        ProxiedPlayer player = proxyClient.getRedirector();

        if (player != null) {
            player.disableAutoReconnect();
            player.useClient(null);
            player.sendTitle(new BungeeTitle().title(TextComponent.fromLegacyText("§7Connecting to")).subTitle(TextComponent.fromLegacyText("§e" + address + "§7...")).stay(200));
        }

        proxyClient.disconnect();

        ConnectedProxyClient newClient = new ConnectedProxyClient();

        newClient.setAuthentication(proxyClient.getAuthentication(), proxyClient.getCredentials());

        return newClient.connect(address, null).thenAccept(success -> {
            if (player != null) {
                player.sendTitle(new BungeeTitle().reset());
                player.enableAutoReconnect();
                if (success) {
                    MCProxy.getInstance().switchClientSafe(player, newClient);
                } else {
                    this.fallback(player, proxyClient, null);
                }
            }
        }).exceptionally(throwable -> {
            if (player != null) {
                player.sendTitle(new BungeeTitle().reset());
                player.sendActionBar(200, TextComponent.fromLegacyText(throwable.getMessage().replace('\n', ' ')));
                this.fallback(player, proxyClient, throwable);
            }
            return null;
        });
    }

    private void fallback(ProxiedPlayer player, ConnectedProxyClient oldClient, Throwable reason) {
        ConnectedProxyClient nextClient = MCProxy.getInstance().findBestProxyClient(player.getUniqueId());
        if (nextClient == null || nextClient.equals(oldClient)) {
            player.disconnect(Constants.MESSAGE_PREFIX + "Failed to connect, no fallback client found. Reason: \n" + (reason != null ? reason.getMessage() : "Unknown reason"));
            return;
        }

        player.sendTitle(new BungeeTitle()
                .title(TextComponent.fromLegacyText("§cFailed to connect"))
                .subTitle(TextComponent.fromLegacyText(reason != null ? reason.getClass().getSimpleName() : "Unknown reason"))
        );

        player.useClient(nextClient);
    }

}
