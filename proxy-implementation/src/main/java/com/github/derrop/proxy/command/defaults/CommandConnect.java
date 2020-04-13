package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.mojang.authlib.exceptions.AuthenticationException;

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

            ServiceConnection proxyClient = player.getConnectedClient();
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

            Collection<ServiceConnection> clients = MCProxy.getInstance().getOnlineClients().stream()
                    .filter(proxyClient -> args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase(proxyClient.getName()))
                    .collect(Collectors.toList());

            if (clients.isEmpty()) {
                sender.sendMessage("§cNo client matching found");
                return;
            }

            CountDownLatch countDownLatch = new CountDownLatch(clients.size());

            for (ServiceConnection client : clients) {
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

    private CompletableFuture<Void> connect(ServiceConnection connection, NetworkAddress address) {
        if (connection.getServerAddress().equals(address)) {
            return CompletableFuture.completedFuture(null);
        }

        ProxiedPlayer player = connection.getPlayer();

        if (player != null) {
            player.disableAutoReconnect();
            player.useClient(null);

            ProvidedTitle title = MCProxy.getInstance()
                    .createTitle()
                    .title("§7Connecting to")
                    .subTitle("§e" + address + "§7...")
                    .stay(200);
            player.sendTitle(title);
        }

        try {
            ServiceConnection newClient = connection.getProxy().createConnection(connection.getCredentials(), address);

            try {
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return newClient.connect().thenAccept(success -> {
                if (player != null) {
                    player.sendTitle(MCProxy.getInstance().createTitle().reset());
                    player.enableAutoReconnect();
                    if (success) {
                        player.useClientSafe(newClient);
                    } else {
                        this.fallback(player, connection, null);
                    }
                }
            }).exceptionally(throwable -> {
                if (player != null) {
                    player.sendTitle(MCProxy.getInstance().createTitle().reset());
                    player.sendActionBar(200, TextComponent.fromLegacyText(throwable.getMessage().replace('\n', ' ')));
                    this.fallback(player, connection, throwable);
                }
                return null;
            });
        } catch (AuthenticationException exception) {
            exception.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

    private void fallback(ProxiedPlayer player, ServiceConnection oldClient, Throwable reason) {
        ServiceConnection nextClient = MCProxy.getInstance().findBestConnection(player);
        if (nextClient == null || nextClient.equals(oldClient)) {
            player.disconnect(Constants.MESSAGE_PREFIX + "Failed to connect, no fallback client found. Reason: \n" + (reason != null ? reason.getMessage() : "Unknown reason"));
            return;
        }

        player.getProxy().createTitle()
                .title(TextComponent.fromLegacyText("§cFailed to connect"))
                .subTitle(TextComponent.fromLegacyText(reason != null ? reason.getClass().getSimpleName() : "Unknown reason"))
                .send(player);

        player.useClient(nextClient);
    }

}
