package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.repository.PlayerRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CommandList extends NonTabCompleteableCommandCallback {

    public CommandList() {
        super("proxy.command.list", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        Collection<? extends ServiceConnection> clients = MCProxy.getInstance().getOnlineClients();
        commandSender.sendMessage("Connected clients: (" + clients.size() + ")");

        for (ServiceConnection onlineClient : clients) {
            commandSender.sendMessage("- §e" + onlineClient.getName() + " §7(" + (onlineClient.getPlayer() != null ? "§cnot free" : "§afree") + "§7); Connected on: " + onlineClient.getServerAddress());
        }

        commandSender.sendMessage(" ");

        Collection<Player> players = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PlayerRepository.class).getOnlinePlayers();
        commandSender.sendMessage("Connected users: (" + players.size() + ")");

        for (Player player : players) {
            commandSender.sendMessage("- §e" + player.getName() + " §7(on: " + (player.getConnectedClient() == null ? "§cnone" : "§7" + player.getConnectedClient().getName()) + "§7)");
        }

        return CommandResult.END;
    }
}
