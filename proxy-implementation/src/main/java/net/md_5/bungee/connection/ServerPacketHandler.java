package net.md_5.bungee.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayChat;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import net.md_5.bungee.chat.ComponentSerializer;

public class ServerPacketHandler {

    @PacketHandler
    public void handleGeneral(ConnectedProxyClient client, DecodedPacket packet) {
        client.getEntityMap().rewriteClientbound(packet.getRealByteBuf(), client.getEntityId(), client.getEntityId(), 47);
        client.redirectPacket(packet.getByteBuf(), packet.getPacket());
    }

    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.KEEP_ALIVE)
    public void handleKeepAlive(ConnectedProxyClient client, PacketPlayKeepAlive alive) {
        client.write(alive);
        client.setLastAlivePacket(System.currentTimeMillis());
        throw CancelProceedException.INSTANCE;
    }


    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.LOGIN)
    public void handleLogin(ConnectedProxyClient client, PacketPlayServerLogin login) {
        client.setEntityId(login.getEntityId());
        client.connectionSuccess();
    }


    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.CUSTOM_PAYLOAD)
    public void handlePluginMessage(ConnectedProxyClient client, PacketPlayPluginMessage pluginMessage) {
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

    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.KICK_DISCONNECT)
    public void handleKick(ConnectedProxyClient client, PacketPlayServerKickPlayer kick) throws Exception {
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        client.handleDisconnect(reason);

        client.setLastKickReason(reason);
        MCProxy.getInstance().unregisterConnection(client.getConnection());

        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.CHAT)
    public void handle(ConnectedProxyClient client, PacketPlayChat chat) throws Exception {
        ChatEvent event = new ChatEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, ComponentSerializer.parse(chat.getMessage()));
        if (client.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    // TODO Implement TabComplete response?

    @PacketHandler(packetIds = ProtocolIds.ClientBound.Play.RESPAWN)
    public void handle(ConnectedProxyClient client, PacketPlayServerRespawn respawn) {
        client.setDimension(respawn.getDimension());
    }
}