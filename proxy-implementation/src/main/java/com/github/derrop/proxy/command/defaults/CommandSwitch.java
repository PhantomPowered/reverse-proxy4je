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

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandSwitch extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;
    
    public CommandSwitch(ServiceRegistry registry) {
        super("proxy.command.switch", null);
        this.registry = registry;
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

            for (ServiceConnection freeClient : this.registry.getProviderUnchecked(ServiceConnector.class).getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        Optional<? extends ServiceConnection> optionalClient = this.registry.getProviderUnchecked(ServiceConnector.class)
                .getFreeClients()
                .stream()
                .filter(proxyClient -> arguments[0].equalsIgnoreCase(proxyClient.getName()))
                .findFirst();
        if (!optionalClient.isPresent()) {
            commandSender.sendMessage("§cThat account does not exist, available:");
            for (ServiceConnection freeClient : this.registry.getProviderUnchecked(ServiceConnector.class).getFreeClients()) {
                commandSender.sendMessage("- " + freeClient.getName());
            }

            return CommandResult.END;
        }

        //((Player) commandSender).useClient(optionalClient.get()); TODO not working when two clients on the same proxy are connected in the same world
        ((Player) commandSender).useClientSafe(optionalClient.get());
        return CommandResult.END;
    }
}
