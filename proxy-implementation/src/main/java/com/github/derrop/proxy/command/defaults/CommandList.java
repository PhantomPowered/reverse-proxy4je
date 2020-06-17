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
import com.github.derrop.proxy.api.connection.player.PlayerRepository;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CommandList extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;
    
    public CommandList(ServiceRegistry registry) {
        super("proxy.command.list", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        Collection<? extends ServiceConnection> clients = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients();
        commandSender.sendMessage("Connected clients: (" + clients.size() + ")");

        for (ServiceConnection onlineClient : clients) {
            commandSender.sendMessage("- §e" + onlineClient.getName() + " §7(" + (onlineClient.getPlayer() != null ? "§cnot free" : "§afree") + "§7); Connected on: " + onlineClient.getServerAddress());
        }

        commandSender.sendMessage(" ");

        Collection<Player> players = this.registry.getProviderUnchecked(PlayerRepository.class).getOnlinePlayers();
        commandSender.sendMessage("Connected users: (" + players.size() + ")");

        for (Player player : players) {
            commandSender.sendMessage("- §e" + player.getName() + " §7(on: " + (player.getConnectedClient() == null ? "§cnone" : "§7" + player.getConnectedClient().getName()) + "§7)");
        }

        return CommandResult.END;
    }
}
