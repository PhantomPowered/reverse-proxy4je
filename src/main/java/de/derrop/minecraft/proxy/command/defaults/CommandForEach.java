package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import net.md_5.bungee.connection.ProxiedPlayer;

import java.util.Arrays;

public class CommandForEach extends Command {

    public CommandForEach() {
        super("foreach");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("This command is only available for players");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage("foreach <execute> <message> | execute a command for every player on the proxy (§e{name} §7for the name of the player)");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equalsIgnoreCase("execute")) {

            sender.sendMessage("Executing the commands...");
            ConnectedProxyClient selfClient = ((ProxiedPlayer) sender).getConnectedClient();
            for (ConnectedProxyClient onlineClient : MCProxy.getInstance().getOnlineClients()) {
                if (selfClient == null || !onlineClient.getAccountUUID().equals(selfClient.getAccountUUID())) {
                    ((ProxiedPlayer) sender).chat(message.replace("{name}", onlineClient.getAccountName()));
                }
            }

        }
    }
}
