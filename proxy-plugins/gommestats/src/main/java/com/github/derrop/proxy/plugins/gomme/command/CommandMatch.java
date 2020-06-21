package com.github.derrop.proxy.plugins.gomme.command;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandMatch extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;
    private final MatchManager matchManager;

    public CommandMatch(ServiceRegistry registry, MatchManager matchManager) {
        super("command.match", null);
        this.registry = registry;
        this.matchManager = matchManager;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (args.length == 2 && args[0].equalsIgnoreCase("createLog")) {
            for (ServiceConnection connection : this.filterConnections(sender, args[1])) {
                MatchInfo matchInfo = this.matchManager.getMatch(connection);
                if (matchInfo == null) {
                    sender.sendMessage(" - " + connection.getName() + ": No match found");
                    continue;
                }
                sender.sendMessage(" - " + connection.getName() + ": " + this.matchManager.createPaste(matchInfo));
            }

            return CommandResult.SUCCESS;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            for (ServiceConnection connection : this.filterConnections(sender, args[1])) {
                MatchInfo matchInfo = this.matchManager.getMatch(connection);
                if (matchInfo == null) {
                    sender.sendMessage(" - " + connection.getName() + ": None");
                    continue;
                }

                sender.sendMessage(" - " + connection.getName() + ": " + matchInfo.getGameMode().getDisplayName());
                sender.sendMessage(" * Begin: " + (matchInfo.getBeginTimestamp() == -1 ? "None" : MatchInfo.FORMAT.format(matchInfo.getBeginTimestamp())));
                sender.sendMessage(" * End: " + (matchInfo.getEndTimestamp() == -1 ? "None" : MatchInfo.FORMAT.format(matchInfo.getEndTimestamp())));

                TextComponent component = TextComponent.of(Constants.MESSAGE_PREFIX + " * §7[§aCreate log§7]")
                        .clickEvent(ClickEvent.runCommand("/proxy match createLog " + connection.getName()))
                        .hoverEvent(HoverEvent.showText(TextComponent.of("§7Click to create the log for this match")));
                sender.sendMessage(component);

            }

            return CommandResult.SUCCESS;
        } else {
            this.sendHelp(sender);

            return CommandResult.BREAK;
        }
    }

    private Collection<ServiceConnection> filterConnections(CommandSender sender, String filter) {
        Collection<ServiceConnection> connections = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                .filter(o -> filter.equalsIgnoreCase("all") || filter.equalsIgnoreCase(o.getName()))
                .collect(Collectors.toList());
        if (connections.isEmpty()) {
            sender.sendMessage("No connections for this filter found");
        }
        return connections;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("match createLog <ALL|name>");
        sender.sendMessage("match info <ALL|name>");
    }

}
