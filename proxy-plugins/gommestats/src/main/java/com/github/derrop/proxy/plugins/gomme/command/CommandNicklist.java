package com.github.derrop.proxy.plugins.gomme.command;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.secret.GommeNickDetector;
import com.github.derrop.proxy.plugins.gomme.secret.GommeNickProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CommandNicklist extends NonTabCompleteableCommandCallback {

    private ServiceRegistry registry;
    private GommeNickDetector nickDetector;

    public CommandNicklist(ServiceRegistry registry, GommeNickDetector nickDetector) {
        super("command.nicklist", null);
        this.registry = registry;
        this.nickDetector = nickDetector;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        Collection<? extends ServiceConnection> connections = null;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Execute this command as a player or use 'nicklist <ALL|name>'");
                return CommandResult.BREAK;
            }

            ServiceConnection connection = ((Player) sender).getConnectedClient();
            if (connection == null) {
                sender.sendMessage("You are not connected with any client");
                return CommandResult.BREAK;
            }

            connections = Collections.singletonList(connection);
        } else if (args.length == 1) {
            connections = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                    .filter(o -> args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase(o.getName()))
                    .collect(Collectors.toList());
        }

        if (connections == null) {
            return CommandResult.BREAK;
        }

        for (ServiceConnection connection : connections) {
            Collection<GommeNickProfile> profiles = this.nickDetector.getNickProfiles().values().stream()
                    .filter(profile -> profile.getInvoker().equals(connection))
                    .collect(Collectors.toList());
            if (profiles.isEmpty()) {
                continue;
            }
            sender.sendMessage("§7Nicked players on §e" + connection.getName() + "#" + connection.getUniqueId() + "§7:");
            for (GommeNickProfile profile : profiles) {
                sender.sendMessage(" §7- " + profile.getRealTeam().getPrefix() + profile.getRealName() + profile.getRealTeam().getSuffix() + " §7as " + profile.getNickName());
            }
        }

        sender.sendMessage("Use 'nicklist <ALL|name>' to get the nicks on a specific client");

        return CommandResult.END;
    }
}
