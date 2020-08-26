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
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandChat extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandChat(ServiceRegistry registry) {
        super("proxy.command.chat", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        ServiceConnector connector = this.registry.getProviderUnchecked(ServiceConnector.class);

        if (arguments.length < 2) {
            commandSender.sendMessage("chat <ALL|name> <message> | send a message as a specific user");
            commandSender.sendMessage("Available clients:");
            for (ServiceConnection freeClient : connector.getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        Collection<? extends ServiceConnection> clients = arguments[0].equalsIgnoreCase("all")
                ? connector.getOnlineClients()
                : connector.getOnlineClients().stream().filter(proxyClient -> arguments[0].equalsIgnoreCase(proxyClient.getName())).collect(Collectors.toList());
        if (clients.isEmpty()) {
            commandSender.sendMessage("§cNo client matching the given name found");
            return CommandResult.BREAK;
        }

        String message = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        commandSender.sendMessage("Executing §e\"" + message + "\" §7as §e" + clients.stream().map(ServiceConnection::getName).collect(Collectors.joining(", ")));

        for (ServiceConnection client : clients) {
            client.chat(message);
        }

        return CommandResult.END;
    }
}
