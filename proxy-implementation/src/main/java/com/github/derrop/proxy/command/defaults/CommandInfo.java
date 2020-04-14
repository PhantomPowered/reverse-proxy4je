package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

public class CommandInfo extends NonTabCompleteableCommandCallback {

    public CommandInfo() {
        super("proxy.command.info", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (commandSender instanceof Player) {
            ServiceConnection client = ((Player) commandSender).getConnectedClient();
            commandSender.sendMessage("§7Connected with client: " + (client == null ? "§cNONE" : "§e" + client.getName() + " §7on §e" + client.getServerAddress()));
        }

        commandSender.sendMessage("§7Connected clients: §e" + MCProxy.getInstance().getOnlineClients().size() + " §7(Free: §a" + MCProxy.getInstance().getFreeClients().size() + "§7)");
        return CommandResult.END;
    }
}
