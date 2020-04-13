package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;

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
