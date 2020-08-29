/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.command.defaults;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.connection.DefaultConnectionHandler;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommandConnect extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandConnect(ServiceRegistry registry) {
        super("proxy.command.connect", null);
        this.registry = registry;
    }

    private CompletableFuture<Void> connect(CommandSender sender, ServiceConnection connection, NetworkAddress address) {
        if (connection.getServerAddress().equals(address)) {
            return CompletableFuture.completedFuture(null);
        }

        Player player = connection.getPlayer();
        if (player != null) {
            player.disableAutoReconnect();
            player.useClient(null);

            Title title = Title.of(
                    TextComponent.of("§7Connecting to"),
                    TextComponent.of("§e" + address + "§7..."),
                    Title.Times.of(Duration.ZERO, Duration.ofSeconds(20), Duration.ZERO)
            );
            player.sendTitle(title);
        }

        try {
            ServiceConnection newClient = this.registry.getProviderUnchecked(ServiceConnector.class).createConnection(connection.getCredentials(), address);

            try {
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return newClient.connect().addListener(DefaultConnectionHandler.coloredCommand(newClient, sender)).thenAccept(result -> {
                if (player != null) {
                    player.resetTitle();
                    player.enableAutoReconnect();
                    if (result.isSuccess()) {
                        player.useClientSafe(newClient);
                    } else {
                        this.fallback(player, connection, null);
                    }
                }
            }).exceptionally(throwable -> {
                if (player != null) {
                    player.resetTitle();
                    Component reason = TextComponent.of(throwable.getMessage().replace('\n', ' '));
                    player.sendActionBar(200, reason);
                    this.fallback(player, connection, reason);
                }
                return null;
            });
        } catch (AuthenticationException exception) {
            exception.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

    private void fallback(Player player, ServiceConnection oldClient, Component reason) {
        ServiceConnection nextClient = this.registry.getProviderUnchecked(ServiceConnector.class).findBestConnection(player.getUniqueId());
        if (nextClient == null || nextClient.equals(oldClient)) {
            player.disconnect(TextComponent.of(APIUtil.MESSAGE_PREFIX + "Failed to connect, no fallback client found. Reason: \n").append(reason == null ? TextComponent.of("§cUnknown reason") : reason));
            return;
        }

        Title title = Title.of(
                TextComponent.of("§cFailed to connect"),
                TextComponent.of("§7see the actionbar for the reason")
        );
        player.sendTitle(title);
        player.useClient(nextClient);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length == 0) {
            commandSender.sendMessage("connect [ALL|name] <host>");
            return CommandResult.BREAK;
        }

        if (arguments.length != 2 && !(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        if (arguments.length == 1) {
            Player player = (Player) commandSender;

            ServiceConnection proxyClient = player.getConnectedClient();
            if (proxyClient == null) {
                commandSender.sendMessages("You are not connected with any client");
                return CommandResult.BREAK;
            }

            NetworkAddress address = NetworkAddress.parse(arguments[0]);
            if (address == null) {
                commandSender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            this.connect(commandSender, proxyClient, address);
        } else {
            NetworkAddress address = NetworkAddress.parse(arguments[1]);
            if (address == null) {
                commandSender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            Collection<ServiceConnection> clients = this.registry.getProviderUnchecked(ServiceConnector.class)
                    .getOnlineClients().stream()
                    .filter(proxyClient -> arguments[0].equalsIgnoreCase("all") || arguments[0].equalsIgnoreCase(proxyClient.getName()))
                    .collect(Collectors.toList());

            if (clients.isEmpty()) {
                commandSender.sendMessage("§cNo client matching found");
                return CommandResult.BREAK;
            }

            CountDownLatch countDownLatch = new CountDownLatch(clients.size());
            for (ServiceConnection client : clients) {
                APIUtil.EXECUTOR_SERVICE.execute(() -> {
                    try {
                        this.connect(commandSender, client, address).get(10, TimeUnit.SECONDS);
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

            commandSender.sendMessage("§aDone");
        }

        return CommandResult.END;
    }
}
