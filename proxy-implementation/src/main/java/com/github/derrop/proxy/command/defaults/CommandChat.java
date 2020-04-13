package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandChat extends NonTabCompleteableCommandCallback {

    public CommandChat() {
        super("proxy.command.chat", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length < 2) {
            commandSender.sendMessage("chat <ALL|name> <message> | send a message as a specific user");
            commandSender.sendMessage("Available clients:");
            for (ServiceConnection freeClient : MCProxy.getInstance().getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        Collection<? extends ServiceConnection> clients = arguments[0].equalsIgnoreCase("all")
                ? MCProxy.getInstance().getOnlineClients()
                : MCProxy.getInstance().getOnlineClients().stream().filter(proxyClient -> arguments[0].equalsIgnoreCase(proxyClient.getName())).collect(Collectors.toList());
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
