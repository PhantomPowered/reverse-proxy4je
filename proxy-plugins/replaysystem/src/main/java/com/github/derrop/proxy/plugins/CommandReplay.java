package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandReplay extends NonTabCompleteableCommandCallback {

    public CommandReplay(@NotNull String permission, @Nullable BaseComponent[] help) {
        super(permission, help);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        return CommandResult.END;
    }
/*
    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public CommandReplay() {
        super("replay");
        super.setPermission("command.replay");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length == 0) {
            UUID id = UUID.randomUUID();
            ((ProxiedPlayer) sender).sendPacket(new PlayerListItem(
                    PlayerListItem.Action.ADD_PLAYER,
                    new PlayerListItem.Item[]{
                            new PlayerListItem.Item(
                                    id, "TEST_NAME",
                                    new String[][] {
                                            new String[]{"textures", "eyJ0aW1lc3RhbXAiOjE1ODY2MzAyNDg2ODQsInByb2ZpbGVJZCI6ImFmN2M3YWFiMmRhOTRlYmI4MWZlN2I0NGUxYjhiNTBjIiwicHJvZmlsZU5hbWUiOiJkZXJyb3AiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVlZmI0NjQzMjRkNzlmZjUyNGNlNjIwYTUzNzNkODkwMjY5ODI2NjczNjY2ODg1NjBhNGE1ZWZiN2QzM2I4NzUifX19", "bONmCbEnrKzF5h20M/aiqCFYSl/LCa2gvVXdDR7ndfpYyv9BQTZaksb1NvBo21YueewVxMlz6OOMSbCN74mCYOEoC+G+bm0DMiOr5aU7ZOSqXnmm09H//4/uqKaehpyQbRNFbqR1kGXweznhFAaxP+iceNgcK4bGwAQ5axPwxz86vNcyojUPwbBr+vi5nX3McAKLw7ht6rAg6JIxR3SgGUxXDJHArFxTpFE3zyc31mOUwrs78IgyYoapXwAQS5+OfmkY/YCBbDITUuBkYgMcAlMt1jeylR9C9Qg1eJ6KclTlu/AVUZwkrorXCQnEZfagYsflPkL6DT6lb9X7Si7M+ECskRb6S9GJ12pDtmqiACrwEtW7gPRon9geWA2Jz6+OqUlUX8g/PiDIbW/F3wNCSRXncQ4Qw/9f4fUCjvQLrgfDuuk3GBYxlQyQVZBz7OEgFwyHGwg3GMLYdAVyE829CyqH/kptR+37/M8mWwjjIWP/R6HkkB5HggzRM971BuyMFaU97i1CYhBHgjr5JqU2MdrbOcqJ8hWa/CLI0uI57a+9heTYgB/vyxrQR3v5AwAEvZOfLuDJkjAyMrfSHWw7q+vxiUFJODLogn1ACcp/Az+LRYn7tztfrJXkEGROLj6CqPj0TvLBEHjc7htrkG1NrBPRU0CrFT1rvRT6koqqvH4="}
                                    },
                                    1, 0,
                                    "DisplayTEST"

                            )
                    }
            ));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            ((ProxiedPlayer) sender).sendPacket(new SpawnPlayer(
                    19357, id,
                    PlayerPositionPacketUtil.getFixLocation(-378), PlayerPositionPacketUtil.getFixLocation(68), PlayerPositionPacketUtil.getFixLocation(422),
                    (byte) 0, (byte) 0,
                    (short) 0, new ArrayList<>()
            ));
            return;
        }

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
                    ServiceConnection proxyClient = ((UserConnection) sender).getConnectedClient();
                    ((UserConnection) sender).useClient(null);

                    MCProxy.getInstance().getReplaySystem().playReplayAsync((UserConnection) sender, replayId, replayInfo -> {
                        sender.sendMessage("Playing replay " + replayId + " on " + replayInfo.getServerAddress());
                    }).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage("Successfully played the replay with the id " + replayId);
                        } else {
                            sender.sendMessage("Cannot load replay with id " + replayId);
                        }
                        ((UserConnection) sender).useClient(proxyClient);
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
                UUID replayId = MCProxy.getInstance().getReplaySystem().recordReplay(((UserConnection) sender).getConnectedClient(), sender.getUniqueId(), sender.getName());
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
    }*/

}
