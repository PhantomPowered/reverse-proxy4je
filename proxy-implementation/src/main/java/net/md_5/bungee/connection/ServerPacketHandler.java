package net.md_5.bungee.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.service.TitleReceiveEvent;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerChatMessage;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTitle;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPluginMessage;
import net.md_5.bungee.chat.ComponentSerializer;

public class ServerPacketHandler {

    @PacketHandler
    public void handleGeneral(ConnectedProxyClient client, DecodedPacket packet) {
        client.redirectPacket(packet.getProtoBuf().clone(), packet.getPacket());
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
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        client.handleDisconnect(reason);

        client.setLastKickReason(reason);
        MCProxy.getInstance().unregisterConnection(client.getConnection());

        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerChatMessage chat) throws Exception {
        ChatEvent event = new ChatEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, ComponentSerializer.parse(chat.getMessage()));
        if (client.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    // TODO Implement TabComplete response?

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.RESPAWN, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerRespawn respawn) {
        client.setDimension(respawn.getDimension());
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
