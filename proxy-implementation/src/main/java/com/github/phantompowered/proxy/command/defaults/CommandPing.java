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

import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.connection.Connection;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

public class CommandPing extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandPing(ServiceRegistry registry) {
        super("proxy.command.ping", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (sender instanceof Player) {
            this.display((Player) sender, sender);
            return CommandResult.SUCCESS;
        }

        sender.sendMessage("Pings of all connected clients");
        for (ServiceConnection client : this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients()) {
            this.display(client, sender);
        }

        return CommandResult.END;
    }

    private void display(Connection connection, CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        if (connection instanceof Player) {
            Player player = (Player) connection;
            ServiceConnection client = player.getConnectedClient();

            builder.append(player.getName()).append(" <- ").append(player.getPing()).append(" ms -> PhantomProxy");
            if (client != null) {
                builder.append('(').append(client.getName()).append(')');
                builder.append(" <- ").append(client.getPing()).append(" -> ").append(client.getServerAddress().getRawHost());
            }
        } else if (connection instanceof ServiceConnection) {
            ServiceConnection client = (ServiceConnection) connection;
            Player player = client.getPlayer();

            if (player != null) {
                builder.append(player.getName()).append(" <- ").append(player.getPing()).append(" ms -> ");
            }

            builder.append("PhantomProxy(").append(client.getName()).append(") <- ").append(client.getPing()).append(" -> ").append(client.getServerAddress().getRawHost());
        }

        sender.sendMessage(builder.toString());
    }

}
