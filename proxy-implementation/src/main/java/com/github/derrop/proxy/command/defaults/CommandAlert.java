package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

public class CommandAlert extends NonTabCompleteableCommandCallback {

    public CommandAlert() {
        super("proxy.command.alert", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        String message = "§cALERT §8| §7" + ChatColor.translateAlternateColorCodes('&', String.join(" ", arguments));

        for (ServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
            if (onlineClient.getPlayer() != null) {
                onlineClient.getPlayer().sendMessage("");
                onlineClient.getPlayer().sendMessage(message);
                onlineClient.getPlayer().sendMessage("");
            }
        }

        return CommandResult.END;
    }
}
