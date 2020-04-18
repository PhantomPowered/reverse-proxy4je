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
package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommandConnect extends NonTabCompleteableCommandCallback {

    public CommandConnect() {
        super("proxy.command.connect", null);
    }

    private CompletableFuture<Void> connect(ServiceConnection connection, NetworkAddress address) {
        if (connection.getServerAddress().equals(address)) {
            return CompletableFuture.completedFuture(null);
        }

        Player player = connection.getPlayer();

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

    private void fallback(Player player, ServiceConnection oldClient, Throwable reason) {
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

            this.connect(proxyClient, address);
        } else {
            NetworkAddress address = NetworkAddress.parse(arguments[1]);
            if (address == null) {
                commandSender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            Collection<ServiceConnection> clients = MCProxy.getInstance().getOnlineClients().stream()
                    .filter(proxyClient -> arguments[0].equalsIgnoreCase("all") || arguments[0].equalsIgnoreCase(proxyClient.getName()))
                    .collect(Collectors.toList());

            if (clients.isEmpty()) {
                commandSender.sendMessage("§cNo client matching found");
                return CommandResult.BREAK;
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

            commandSender.sendMessage("§aDone");
        }

        return CommandResult.END;
    }
}
