package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.ProxiedPlayer;

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
            for (ConnectedProxyClient freeClient : MCProxy.getInstance().getFreeClients()) {
                sender.sendMessage("- " + freeClient.getAccountName());
            }
            return;
        }

        Optional<ConnectedProxyClient> optionalClient = MCProxy.getInstance().getFreeClients().stream()
                .filter(proxyClient -> proxyClient.getAccountName().equalsIgnoreCase(args[0]))
                .findFirst();
        if (!optionalClient.isPresent()) {
            sender.sendMessage("§cThat account does not exist, available:");
            for (ConnectedProxyClient freeClient : MCProxy.getInstance().getFreeClients()) {
                sender.sendMessage("- " + freeClient.getAccountName());
            }
            return;
        }

        ((ProxiedPlayer) sender).disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + optionalClient.get().getAccountName()));
        MCProxy.getInstance().setReconnectTarget(sender.getUniqueId(), optionalClient.get().getAccountUUID());
    }
}
