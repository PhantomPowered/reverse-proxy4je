package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandForEach extends NonTabCompleteableCommandCallback {

    public CommandForEach() {
        super("proxy.command.foreach", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        if (arguments.length < 2) {
            commandSender.sendMessage("foreach EXECUTE <message> | execute a command for every player on the proxy (§e{name} §7for the name of the player)");
            return CommandResult.BREAK;
        }

        String message = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        if (arguments[0].equalsIgnoreCase("execute")) {
            commandSender.sendMessage("Executing the commands...");
            ServiceConnection selfClient = ((ProxiedPlayer) commandSender).getConnectedClient();
            for (ServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
                if (selfClient == null || (onlineClient.getUniqueId() != null && !onlineClient.getUniqueId().equals(selfClient.getUniqueId()))) {
                    ((ProxiedPlayer) commandSender).chat(message.replace("{name}", onlineClient.getName() == null ? "null" : onlineClient.getName()));
                }
            }
        }

        return CommandResult.END;
    }
}
