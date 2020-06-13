package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.*;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.service.TitleReceiveEvent;
import com.github.derrop.proxy.api.events.connection.service.entity.EntityMoveEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.api.util.Side;
import com.github.derrop.proxy.connection.AppendedActionBar;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.DefaultServiceConnector;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerPluginMessage;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerTitle;
import com.github.derrop.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

public class ServerPacketHandler {

    @PacketHandler(directions = ProtocolDirection.TO_CLIENT)
    public void handleGeneral(ConnectedProxyClient client, DecodedPacket packet) {
        client.redirectPacket(packet.getProtoBuf().clone(), packet.getPacket());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING)
    public void modifyActionBarRedirect(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        this.modifyActionBar(client, packet);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.PLAY)
    public void modifyActionBarPlay(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        this.modifyActionBar(client, packet);
    }

    private void modifyActionBar(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        if (client.getRedirector() == null) {
            return;
        }
        DefaultPlayer player = (DefaultPlayer) client.getRedirector();
        if (packet.getPosition() != ChatMessageType.ACTION_BAR.ordinal()) {
            return;
        }

        Component component = GsonComponentSerializer.INSTANCE.deserialize(packet.getMessage());
        String original = LegacyComponentSerializer.legacy().serialize(component);

        for (AppendedActionBar actionBar : player.getActionBars()) {
            String message = actionBar.getMessage().get();
            if (message == null) {
                player.getActionBars().remove(actionBar);
                continue;
            }
            Side side = actionBar.getSide();
            original = side == Side.LEFT ? message + original : original + message;
        }

        packet.setMessage(GsonComponentSerializer.INSTANCE.serialize(TextComponent.of(original)));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ENTITY_TELEPORT, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityTeleport(ConnectedProxyClient client, PacketPlayServerEntityTeleport teleport) {
        Location location = teleport.getLocation();

        if (teleport.getEntityId() != client.getEntityId()) {
            Entity entity = client.getConnection().getWorldDataProvider().getEntityInWorld(teleport.getEntityId());
            if (entity == null) {
                return;
            }
            client.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new EntityMoveEvent(client.getConnection(), entity, entity.getLocation(), location));
            return;
        }

        client.getConnection().updateLocation(location);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ENTITY_METADATA, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityMeta(ConnectedProxyClient client, PacketPlayServerEntityMetadata meta) {
        if (client.getEntityId() == meta.getEntityId()) {
            return;
        }

        Entity entity = client.getConnection().getWorldDataProvider().getEntityInWorld(client.getEntityId());
        if (entity == null) {
            return;
        }

        entity.getCallable().handleEntityPacket(meta);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.POSITION, directions = ProtocolDirection.TO_CLIENT)
    public void handlePosition(ConnectedProxyClient client, PacketPlayServerPosition position) {
        Location location = new Location(position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
        client.getConnection().updateLocation(location);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.KEEP_ALIVE, directions = ProtocolDirection.TO_CLIENT)
    public void handleKeepAlive(ConnectedProxyClient client, PacketPlayKeepAlive alive) {
        client.write(alive);
        client.setLastAlivePacket(System.currentTimeMillis());
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.LOGIN, directions = ProtocolDirection.TO_CLIENT)
    public void handleLogin(ConnectedProxyClient client, PacketPlayServerLogin login) {
        client.setEntityId(login.getEntityId());
        client.connectionSuccess();
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD, directions = ProtocolDirection.TO_CLIENT)
    public void handlePluginMessage(ConnectedProxyClient client, PacketPlayServerPluginMessage pluginMessage) {
        PluginMessageEvent event = new PluginMessageEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, pluginMessage.getTag(), pluginMessage.getData());
        if (client.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());

        Connection connection = client.getRedirector();
        if (connection == null) {
            return;
        }

        connection.sendPacket(pluginMessage);
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.KICK_DISCONNECT, directions = ProtocolDirection.TO_CLIENT)
    public void handleKick(ConnectedProxyClient client, PacketPlayServerKickPlayer kick) throws Exception {
        Component reason = GsonComponentSerializer.INSTANCE.deserialize(kick.getMessage());

        client.handleDisconnect(reason);
        client.setLastKickReason(reason);

        ServiceConnector connector = client.getProxy().getServiceRegistry().getProviderUnchecked(ServiceConnector.class);
        if (connector instanceof DefaultServiceConnector) {
            ((DefaultServiceConnector) connector).unregisterConnection(client.getConnection());
        }
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerChatMessage chat) throws Exception {
        ChatEvent event = new ChatEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, ChatMessageType.values()[chat.getPosition()], GsonComponentSerializer.INSTANCE.deserialize(chat.getMessage()));
        if (client.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(GsonComponentSerializer.INSTANCE.serialize(event.getMessage()));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_CLIENT)
    public void handleTabComplete(ConnectedProxyClient client, PacketPlayServerTabCompleteResponse response) {
        if (client.getRedirector() == null) {
            return;
        }

        DefaultPlayer player = (DefaultPlayer) client.getRedirector();

        if (player.getLastCommandCompleteRequest() != null) {
            player.setLastCommandCompleteRequest(null);
            response.getCommands().add("/proxy");
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.RESPAWN, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerRespawn respawn) {
        client.setDimension(respawn.getDimension());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.LOGIN, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerLogin login) {
        client.setDimension(login.getDimension());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TITLE, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerTitle title) {
        ServiceConnection connection = client.getConnection();
        TitleReceiveEvent event;
        if (title.getAction() == PacketPlayServerTitle.Action.TITLE) {
            event = new TitleReceiveEvent(connection, title.getText(), TitleReceiveEvent.TitleUpdateType.TITLE);
        } else if (title.getAction() == PacketPlayServerTitle.Action.SUBTITLE) {
            event = new TitleReceiveEvent(connection, title.getText(), TitleReceiveEvent.TitleUpdateType.SUB_TITLE);
        } else if (title.getAction() == PacketPlayServerTitle.Action.TIMES) {
            event = new TitleReceiveEvent(connection, title.getFadeIn(), title.getStay(), title.getFadeOut());
        } else if (title.getAction() == PacketPlayServerTitle.Action.RESET || title.getAction() == PacketPlayServerTitle.Action.CLEAR) {
            event = new TitleReceiveEvent(connection);
        } else {
            return;
        }

        if (connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

}
