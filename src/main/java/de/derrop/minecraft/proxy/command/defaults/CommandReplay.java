package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.replay.ReplayInfo;
import de.derrop.minecraft.proxy.replay.ReplayOutputStream;
import net.md_5.bungee.connection.UserConnection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CommandReplay extends Command {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public CommandReplay() {
        super("replay");
        super.setPermission("command.replay");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Available replays:");
            for (UUID replayId : MCProxy.getInstance().getReplaySystem().listReplays()) {
                ReplayInfo replayInfo = MCProxy.getInstance().getReplaySystem().readReplayInfo(replayId);
                if (replayInfo == null) {
                    // file cannot be opened
                    continue;
                }

                sender.sendMessage("- " + replayInfo.getServerAddress() + " (" + FORMAT.format(new Date(replayInfo.getTimestamp())) + ")");
                sender.sendMessage("  Requested by: " + replayInfo.getCreatorName() + "#" + replayInfo.getCreatorId());
                sender.sendMessage("  Recorded by: " + replayInfo.getRecorder());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            if (!(sender instanceof UserConnection)) {
                sender.sendMessage("This command is only for players");
                return;
            }

            for (UUID replayId : MCProxy.getInstance().getReplaySystem().listReplays()) {
                if (replayId.toString().toLowerCase().contains(args[1].toLowerCase())) {

                    ((UserConnection) sender).useClient(null);

                    MCProxy.getInstance().getReplaySystem().playReplayAsync((UserConnection) sender, replayId, replayInfo -> {
                        sender.sendMessage("Playing replay " + replayId + " on " + replayInfo.getServerAddress());
                    }).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage("Successfully played the replay with the id " + replayId);
                        } else {
                            sender.sendMessage("Cannot load replay with id " + replayId);
                        }
                    });

                    break;
                }
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof UserConnection)) {
                sender.sendMessage("This command is only for players");
                return;
            }

            try {
                UUID replayId = MCProxy.getInstance().getReplaySystem().recordReplay(((UserConnection) sender).getProxyClient(), sender.getUniqueId(), sender.getName());
                sender.sendMessage("Successfully started recording the replay with the id " + replayId);
            } catch (IOException exception) {
                exception.printStackTrace();
                sender.sendMessage("Failed to record the replay: " + exception.getMessage());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
            ConnectedProxyClient proxyClient = MCProxy.getInstance().getOnlineClients().stream()
                    .filter(client -> client.getAccountName().equalsIgnoreCase(args[1]))
                    .findFirst()
                    .orElse(null);

            if (proxyClient == null) {
                sender.sendMessage("No client with that name found");
                return;
            }

            for (Map.Entry<ReplayInfo, ReplayOutputStream> entry : MCProxy.getInstance().getReplaySystem().getRunningReplays().entrySet()) {
                if (entry.getKey().getRecorder().equals(proxyClient.getCredentials())) {
                    sender.sendMessage("Stopping replay requested by " + entry.getKey().getCreatorName() + "#" + entry.getKey().getCreatorId() + " on " + entry.getKey().getServerAddress() + " (" + entry.getKey().getRecorder() + ")");
                    entry.getValue().close();
                    sender.sendMessage("Successfully stopped the replay");
                    return;
                }
            }
        } else {
            this.sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("replay create <name>");
        sender.sendMessage("replay create");
        sender.sendMessage("replay stopId <id>"); // todo
        sender.sendMessage("replay stop <name>");
        sender.sendMessage("replay play <id>");
        sender.sendMessage("replay delete <id>"); // todo
        sender.sendMessage("replay list");
    }

}
