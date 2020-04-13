package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;

import java.util.Optional;

public class CommandSwitch extends Command {

    public CommandSwitch() {
        super("switch");
        super.setPermission("command.switch");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("You have to be a player to execute this command");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage("switch <account> | switch to another account");
            sender.sendMessage("Available clients:");
            for (ServiceConnection freeClient : MCProxy.getInstance().getFreeClients()) {
                sender.sendMessage("- " + freeClient.getName());
            }
            return;
        }

        Optional<ServiceConnection> optionalClient = MCProxy.getInstance().getFreeClients().stream()
                .filter(proxyClient -> args[0].equalsIgnoreCase(proxyClient.getName()))
                .findFirst();
        if (!optionalClient.isPresent()) {
            sender.sendMessage("§cThat account does not exist, available:");
            for (ServiceConnection freeClient : MCProxy.getInstance().getFreeClients()) {
                sender.sendMessage("- " + freeClient.getName());
            }
            return;
        }

        //((ProxiedPlayer) sender).useClient(optionalClient.get());
        MCProxy.getInstance().switchClientSafe((ProxiedPlayer) sender, optionalClient.get());
    }
}
