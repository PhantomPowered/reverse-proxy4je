package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandSwitch extends NonTabCompleteableCommandCallback {

    public CommandSwitch() {
        super("proxy.command.switch", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You have to be a player to execute this command");
            return CommandResult.BREAK;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage("switch <account> | switch to another account");
            commandSender.sendMessage("Available clients:");

            for (ServiceConnection freeClient : MCProxy.getInstance().getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        Optional<ServiceConnection> optionalClient = MCProxy.getInstance()
                .getFreeClients()
                .stream()
                .filter(proxyClient -> arguments[0].equalsIgnoreCase(proxyClient.getName()))
                .findFirst();
        if (!optionalClient.isPresent()) {
            commandSender.sendMessage("§cThat account does not exist, available:");
            for (ServiceConnection freeClient : MCProxy.getInstance().getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        MCProxy.getInstance().switchClientSafe((Player) commandSender, optionalClient.get());
        return CommandResult.END;
    }
}
