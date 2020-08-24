/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.player.Player;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.plugins.replay.ReplayInfo;
import com.github.derrop.proxy.plugins.replay.ReplayOutputStream;
import com.github.derrop.proxy.plugins.replay.ReplaySystem;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CommandReplay extends NonTabCompleteableCommandCallback {

    public CommandReplay(@NotNull String permission, @Nullable Component help) {
        super(permission, help);
    }

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private ServiceRegistry registry;

    public CommandReplay(ServiceRegistry registry) {
        super("command.replay", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (args.length == 0) {
            UUID id = UUID.randomUUID();
            ((Player) sender).sendPacket(new PacketPlayServerPlayerInfo(
                    PacketPlayServerPlayerInfo.Action.ADD_PLAYER,
                    new PacketPlayServerPlayerInfo.Item[]{
                            new PacketPlayServerPlayerInfo.Item(
                                    id, "TEST_NAME",
                                    new String[][]{
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
            ((Player) sender).sendPacket(new PacketPlayServerNamedEntitySpawn(
                    19357, id,
                    new Location(-378, 68, 422, 0F, 0F),
                    (short) 0, new ArrayList<>()
            ));
            return CommandResult.BREAK;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Available replays:");
            for (UUID replayId : this.registry.getProviderUnchecked(ReplaySystem.class).listReplays()) {
                ReplayInfo replayInfo = this.registry.getProviderUnchecked(ReplaySystem.class).readReplayInfo(replayId);
                if (replayInfo == null) {
                    // file cannot be opened
                    continue;
                }

                sender.sendMessage("- " + replayInfo.getServerAddress() + " (" + FORMAT.format(new Date(replayInfo.getTimestamp())) + ")");
                sender.sendMessage("  Requested by: " + replayInfo.getCreatorName() + "#" + replayInfo.getCreatorId());
                sender.sendMessage("  Recorded by: " + replayInfo.getRecorder());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command is only for players");
                return CommandResult.BREAK;
            }

            for (UUID replayId : this.registry.getProviderUnchecked(ReplaySystem.class).listReplays()) {
                if (replayId.toString().toLowerCase().contains(args[1].toLowerCase())) {
                    ServiceConnection proxyClient = ((Player) sender).getConnectedClient();
                    ((Player) sender).useClient(null);

                    this.registry.getProviderUnchecked(ReplaySystem.class).playReplayAsync((Player) sender, replayId, replayInfo -> {
                        sender.sendMessage("Playing replay " + replayId + " on " + replayInfo.getServerAddress());
                    }).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage("Successfully played the replay with the id " + replayId);
                        } else {
                            sender.sendMessage("Cannot load replay with id " + replayId);
                        }
                        ((Player) sender).useClient(proxyClient);
                    });

                    break;
                }
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command is only for players");
                return CommandResult.BREAK;
            }

            try {
                UUID replayId = this.registry.getProviderUnchecked(ReplaySystem.class).recordReplay((BasicServiceConnection) ((Player) sender).getConnectedClient(), sender.getUniqueId(), sender.getName());
                sender.sendMessage("Successfully started recording the replay with the id " + replayId);
            } catch (IOException exception) {
                exception.printStackTrace();
                sender.sendMessage("Failed to record the replay: " + exception.getMessage());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
            ServiceConnection proxyClient = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                    .filter(client -> client.getName() != null && client.getName().equalsIgnoreCase(args[1]))
                    .findFirst()
                    .orElse(null);

            if (proxyClient == null) {
                sender.sendMessage("No client with that name found");
                return CommandResult.BREAK;
            }

            for (Map.Entry<ReplayInfo, ReplayOutputStream> entry : this.registry.getProviderUnchecked(ReplaySystem.class).getRunningReplays().entrySet()) {
                if (entry.getKey().getRecorder().equals(proxyClient.getCredentials())) {
                    sender.sendMessage("Stopping replay requested by " + entry.getKey().getCreatorName() + "#" + entry.getKey().getCreatorId() + " on " + entry.getKey().getServerAddress() + " (" + entry.getKey().getRecorder() + ")");
                    entry.getValue().close();
                    sender.sendMessage("Successfully stopped the replay");
                    return CommandResult.BREAK;
                }
            }
        } else {
            this.sendHelp(sender);
            return CommandResult.BREAK;
        }
        return CommandResult.END;
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
