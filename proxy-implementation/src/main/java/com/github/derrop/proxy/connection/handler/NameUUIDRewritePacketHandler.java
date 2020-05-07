package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventPriority;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientChatMessage;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientTabCompleteRequest;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardScore;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class NameUUIDRewritePacketHandler {

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CHAT, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.SECOND)
    public void rewriteChatInput(DefaultPlayer player, PacketPlayClientChatMessage packet) {
        this.acceptNames(player, (clientName, serverName) -> packet.setMessage(this.replaceNames(packet.getMessage(), clientName, serverName)));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewriteChatOutput(ServiceConnection connection, PacketPlayServerChatMessage packet) {
        this.acceptNames(connection, (clientName, serverName) -> packet.setMessage(this.replaceNames(packet.getMessage(), serverName, clientName)));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.NAMED_ENTITY_SPAWN, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewriteNamedEntitySpawn(ServiceConnection connection, PacketPlayServerNamedEntitySpawn packet) {
        if (connection.getPlayer() != null && packet.getPlayerId() == connection.getUniqueId()) {
            packet.setPlayerId(connection.getPlayer().getUniqueId());
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.PLAYER_INFO, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewritePlayerInfo(ServiceConnection connection, PacketPlayServerPlayerInfo packet) {
        this.acceptNames(connection, (clientName, serverName) -> {
            for (PacketPlayServerPlayerInfo.Item item : packet.getItems()) {
                if (item.getUsername() != null && item.getUsername().equals(serverName)) {
                    item.setUsername(clientName);
                }
                if (item.getDisplayName() != null) {
                    item.setDisplayName(item.getDisplayName().replace(serverName, clientName));
                }
                if (connection.getPlayer() != null && item.getUniqueId().equals(connection.getUniqueId())) {
                    item.setUniqueId(connection.getPlayer().getUniqueId());
                }
            }
        });
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewriteTabCompleteResponse(ServiceConnection connection, PacketPlayServerTabCompleteResponse packet) {
        this.acceptNames(connection, (clientName, serverName) -> {
            List<String> result = new ArrayList<>(packet.getCommands().size());
            for (String command : packet.getCommands()) {
                result.add(command.replace(serverName, clientName));
            }
            packet.setCommands(result);
        });
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.FIRST)
    public void rewriteTabCompleteRequest(DefaultPlayer player, PacketPlayClientTabCompleteRequest packet) {
        this.acceptNames(player, (clientName, serverName) -> {
            boolean spaceAtEnd = packet.getCursor().endsWith(" ");
            String[] args = packet.getCursor().split(" ");
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (clientName.equalsIgnoreCase(arg)) {
                    args[i] = serverName;
                }
            }
            packet.setCursor(String.join(" ", args) + (spaceAtEnd ? " " : ""));
        });
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.SCOREBOARD_SCORE, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewriteScoreboardScore(ServiceConnection connection, PacketPlayServerScoreboardScore packet) {
        this.acceptNames(connection,(clientName, serverName) -> packet.setItemName(this.replaceNames(packet.getItemName(), serverName, clientName)));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.SCOREBOARD_TEAM, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING, priority = EventPriority.FIRST)
    public void rewriteScoreboardTeam(ServiceConnection connection, PacketPlayServerScoreboardTeam packet) {
        this.acceptNames(connection, (clientName, serverName) -> {
            if (packet.getPlayers() != null) {
                for (int i = 0; i < packet.getPlayers().length; i++) {
                    packet.getPlayers()[i] = packet.getPlayers()[i].replace(serverName, clientName);
                }
            }
            if (packet.getPrefix() != null) {
                packet.setPrefix(this.replaceNames(packet.getPrefix(), serverName, clientName));
            }
            if (packet.getSuffix() != null) {
                packet.setSuffix(this.replaceNames(packet.getSuffix(), serverName, clientName));
            }
            if (packet.getDisplayName() != null) {
                packet.setDisplayName(this.replaceNames(packet.getDisplayName(), serverName, clientName));
            }
        });
    }

    private String replaceNames(String input, String pattern, String replacement) {
        return input.replaceAll("(?i)" + pattern, replacement);
    }

    private void acceptNames(DefaultPlayer player, BiConsumer<String, String> consumer) {
        if (player != null && player.getConnectedClient() != null) {
            consumer.accept(player.getName(), player.getConnectedClient().getName());
        }
    }

    private void acceptNames(ServiceConnection connection, BiConsumer<String, String> consumer) {
        if (connection != null && connection.getPlayer() != null) {
            consumer.accept(connection.getPlayer().getName(), connection.getName());
        }
    }

}
