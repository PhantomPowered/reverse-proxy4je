package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;

import java.util.Arrays;

public class CommandForEach extends Command {

    public CommandForEach() {
        super("foreach");
        super.setPermission("command.foreach");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("This command is only available for players");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage("foreach EXECUTE <message> | execute a command for every player on the proxy (§e{name} §7for the name of the player)");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equalsIgnoreCase("execute")) {

            sender.sendMessage("Executing the commands...");
            ServiceConnection selfClient = ((ProxiedPlayer) sender).getConnectedClient();
            for (ServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
                if (selfClient == null || (onlineClient.getUniqueId() != null && !onlineClient.getUniqueId().equals(selfClient.getUniqueId()))) {
                    ((ProxiedPlayer) sender).chat(message.replace("{name}", onlineClient.getName() == null ? "null" : onlineClient.getName()));
                }
            }

        }
    }
}
