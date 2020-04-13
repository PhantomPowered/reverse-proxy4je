package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;

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
