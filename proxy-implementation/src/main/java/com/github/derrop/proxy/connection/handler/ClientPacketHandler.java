package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.EventPriority;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.entity.player.DefaultPlayer;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientChatMessage;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientCustomPayload;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientTabCompleteRequest;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientLook;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPosition;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPositionLook;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ClientPacketHandler {

    @PacketHandler(protocolState = ProtocolState.PLAY, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.FIRST)
    public void handleGeneral(DefaultPlayer player, DecodedPacket packet) {
        if (player.getConnectedClient() != null && player.getConnectedClient().isConnected()) {

            if (packet.getPacket() != null) {
                player.getConnectedClient().getEntityRewrite().updatePacketToServer(packet.getPacket(), player.getEntityId(), player.getConnectedClient().getEntityId());

                player.getConnectedClient().getClient().handleClientPacket(packet.getPacket());
            }

            if (packet.getPacket() != null) {
                player.getConnectedClient().getClient().getVelocityHandler().handlePacket(ProtocolDirection.TO_SERVER, packet.getPacket());
            }

            player.getConnectedClient().sendPacket(packet);
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.POSITION, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientPosition position) {
        this.updatePosition(player, position);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.POSITION_LOOK, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientPositionLook position) {
        this.updatePosition(player, position);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.LOOK, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientLook position) {
        this.updatePosition(player, position);
    }

    private void updatePosition(DefaultPlayer player, PacketPlayClientPlayerPosition position) {
        BasicServiceConnection connection = player.getConnectedClient();
        if (connection == null) {
            return;
        }

        Location newLocation = position.getLocation(connection.getLocation());
        connection.updateLocation(newLocation);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CHAT, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY)
    private void handleChat(DefaultPlayer player, PacketPlayClientChatMessage chat) throws Exception {
        int maxLength = (player.getVersion() >= ProtocolIds.Versions.MINECRAFT_1_11) ? 256 : 100;
        if (chat.getMessage().length() >= maxLength) {
            throw CancelProceedException.INSTANCE;
        }

        if (chat.getMessage().startsWith("/proxy ")) {
            try {
                CommandMap commandMap = player.getProxy().getServiceRegistry().getProviderUnchecked(CommandMap.class);
                if (commandMap.process(player, chat.getMessage().replaceFirst("/proxy ", "")) != CommandResult.NOT_FOUND) {
                    throw CancelProceedException.INSTANCE;
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                player.sendMessage("Unable to process command: " + ex.getMessage());
                throw CancelProceedException.INSTANCE;
            }

            return;
        }

        ChatEvent event = new ChatEvent(player, ProtocolDirection.TO_SERVER, LegacyComponentSerializer.legacy().deserialize(chat.getMessage()));
        if (player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(GsonComponentSerializer.INSTANCE.serialize(event.getMessage()));
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CUSTOM_PAYLOAD, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY)
    private void handlePluginMessage(DefaultPlayer player, PacketPlayClientCustomPayload pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(player, ProtocolDirection.TO_SERVER, pluginMessage.getTag(), pluginMessage.getData());
        if (player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_SERVER)
    public void handle(DefaultPlayer player, PacketPlayClientTabCompleteRequest request) {
        if (!request.getCursor().startsWith("/")) {
            return;
        }
        if (!request.getCursor().startsWith("/proxy")) {
            player.setLastCommandCompleteRequest(request.getCursor());
            return;
        }
        List<String> suggestions = player.getProxy().getServiceRegistry().getProviderUnchecked(CommandMap.class).getSuggestions(player, request.getCursor().substring("/proxy ".length()));
        if (!suggestions.isEmpty()) {
            player.sendPacket(new PacketPlayServerTabCompleteResponse(suggestions));
            throw CancelProceedException.INSTANCE;
        }
    }

}
