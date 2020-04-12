package de.derrop.minecraft.proxy.command.defaults;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.chat.component.TextComponent;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.util.NetworkAddress;
import net.md_5.bungee.api.BungeeTitle;

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
            player.sendTitle(new BungeeTitle().title(TextComponent.fromLegacyText("§7Connecting to")).subTitle(TextComponent.fromLegacyText("§e" + address + "§7...")).stay(200));
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
                    player.sendTitle(new BungeeTitle().reset());
                    player.enableAutoReconnect();
                    if (success) {
                        player.useClientSafe(newClient);
                    } else {
                        this.fallback(player, connection, null);
                    }
                }
            }).exceptionally(throwable -> {
                if (player != null) {
                    player.sendTitle(new BungeeTitle().reset());
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
