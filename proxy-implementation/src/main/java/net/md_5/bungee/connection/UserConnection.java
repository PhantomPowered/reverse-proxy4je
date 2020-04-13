package net.md_5.bungee.connection;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.packet.Packet;
import com.github.derrop.proxy.api.events.connection.player.PlayerKickEvent;
import com.github.derrop.proxy.api.util.ChatMessageType;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.connection.PlayerUniqueTabList;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.ChatComponentTransformer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.*;
import net.md_5.bungee.tab.TabList;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.UUID;

public final class UserConnection implements ProxiedPlayer {

    private final MCProxy proxy;

    private final ChannelWrapper ch;

    @Getter
    private final String name;

    @Getter
    private final InitialHandler pendingConnection;

    @Setter
    private BasicServiceConnection connectedClient;

    @Getter
    @Setter
    private int dimension;

    @Getter
    @Setter
    private boolean dimensionChange = true;

    @Getter
    private TabList tabListHandler;

    private boolean autoReconnect = true;

    @Getter
    private String displayName;

    @Getter
    private EntityMap entityRewrite;

    private int compressionThreshold = -1;

    private boolean connected = false;

    public UserConnection(MCProxy proxy, @NotNull ChannelWrapper ch, @NotNull String name, InitialHandler pendingConnection) {
        this.proxy = proxy;
        this.ch = ch;
        this.name = name;
        this.pendingConnection = pendingConnection;
    }

    public ChannelWrapper getCh() {
        return ch;
    }

    public void init() {
        this.entityRewrite = EntityMap.getEntityMap(getPendingConnection().getVersion());
        this.displayName = name;
        this.tabListHandler = new PlayerUniqueTabList(this.ch);

        MCProxy.getInstance().getUUIDStorage().createMapping(this.getUniqueId(), this.getName());
    }

    public int getClientEntityId() {
        return this.connectedClient == null ? 0 : this.connectedClient.getEntityId();
    }

    public int getServerEntityId() {
        return this.getClientEntityId();
    }

    public void sendPacket(PacketWrapper packet) {
        ch.write(packet);
    }

    @Deprecated
    public boolean isActive() {
        return !ch.isClosed();
    }

    @Override
    public Proxy getProxy() {
        return this.proxy;
    }

    @Override
    public void setDisplayName(String name) {
        Preconditions.checkNotNull(name, "displayName");
        displayName = name;
    }

    public void handleDisconnected(ServiceConnection connection, BaseComponent[] reason) {
        if (!this.connected) {
            return;
        }
        this.connected = false;

        if (!this.autoReconnect) {
            this.disconnect(reason);
            return;
        }

        ServiceConnection nextClient = MCProxy.getInstance().findBestConnection(this);
        if (nextClient == null || nextClient.equals(connection)) {
            this.disconnect(Constants.MESSAGE_PREFIX + "Disconnected from " + this.connectedClient.getServerAddress() + ", no fallback client found. Reason:\n§r" + TextComponent.toLegacyText(reason));
            return;
        }
        if (reason != null) {
            this.sendMessage(ChatMessageType.CHAT, reason);
            this.sendActionBar(200, TextComponent.fromLegacyText(TextComponent.toPlainText(reason).replace('\n', ' ')));
        }

        ProvidedTitle title = MCProxy.getInstance()
                .createTitle()
                .title("§cDisconnected")
                .fadeIn(20)
                .stay(100)
                .fadeOut(20);
        this.sendTitle(title);

        this.useClient(nextClient);

        //this.disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "§cDisconnected!\n§7There are §e" + MCProxy.getInstance().getFreeClients() + " free clients §7left!"));
    }

    @Override
    public void useClient(ServiceConnection connection) {
        if (this.ch.isClosing() || this.ch.isClosed()) {
            return;
        }

        if (connection == null) {
            if (this.connectedClient != null) {
                this.connectedClient.getClient().free();
                this.connectedClient = null;
            }
            return;
        }

        if (this.connectedClient != null && this.connectedClient.getCredentials().equals(connection.getCredentials())) {
            this.sendMessage("already connected with this client");
            return;
        }

        if (this.connectedClient != null) {
            this.connectedClient.getClient().free();
        }

        /*for (UUID bossbarId : this.sentBossBars) {
            // Send remove bossbar packet
            this.sendPacket(new net.md_5.bungee.protocol.packet.BossBar(bossbarId, 1));
        }
        this.sentBossBars.clear();*/

        this.tabListHandler.onServerChange();

        this.connected = true;

        ((BasicServiceConnection) connection).getClient().redirectPackets(this, this.connectedClient != null);

        this.sendMessage("§7Your name: §e" + connection.getName());

        this.connectedClient = (BasicServiceConnection) connection;
    }

    @Override
    public BasicServiceConnection getConnectedClient() {
        return this.connectedClient;
    }

    @Override
    public void disableAutoReconnect() {
        this.autoReconnect = false;
    }

    @Override
    public void enableAutoReconnect() {
        this.autoReconnect = true;
    }

    @Override
    public void disconnect(String reason) {
        disconnect0(TextComponent.fromLegacyText(reason));
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        disconnect0(reason);
    }

    @Override
    public void disconnect(BaseComponent reason) {
        disconnect0(reason);
    }

    public void disconnect0(BaseComponent... reason) {
        if (!ch.isClosing()) {
            PlayerKickEvent event = this.proxy.getEventManager().callEvent(new PlayerKickEvent(this, reason));
            if (event.isCancelled()) {
                return;
            }
            if (event.getReason() != null) {
                reason = event.getReason();
            }

            ch.close(new Kick(ComponentSerializer.toString(reason)));

            if (this.connectedClient != null) {
                this.connectedClient.getClient().free();
            }
        }
    }

    @Override
    public void chat(String message) {
        Preconditions.checkState(connectedClient != null, "Not connected to server");
        this.connectedClient.sendPacket(new Chat(message));
    }

    @Deprecated
    private void sendMessage(ChatMessageType position, String message) {
        this.sendPacket(new Chat(message, (byte) position.ordinal()));
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent... message) {
        // transform score components
        message = ChatComponentTransformer.getInstance().transform(this, message);

        if (position == ChatMessageType.ACTION_BAR) {
            // Versions older than 1.11 cannot send the Action bar with the new JSON formattings
            // Fix by converting to a legacy message, see https://bugs.mojang.com/browse/MC-119145
            // derrop: this is a 1.8 proxy
            /*if ( ProxyServer.getInstance().getProtocolVersion() <= ProtocolConstants.MINECRAFT_1_10 )
            {*/
            sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
            /*} else
            {
                net.md_5.bungee.protocol.packet.Title title = new net.md_5.bungee.protocol.packet.Title();
                title.setAction( net.md_5.bungee.protocol.packet.Title.Action.ACTIONBAR );
                title.setText( ComponentSerializer.toString( message ) );
                unsafe.sendPacket( title );
            }*/
        } else {
            sendMessage(position, ComponentSerializer.toString(message));
        }
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent message) {
        message = ChatComponentTransformer.getInstance().transform(this, message)[0];

        // Action bar doesn't display the new JSON formattings, legacy works - send it using this for now
        if (position == ChatMessageType.ACTION_BAR) {
            sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
        } else {
            sendMessage(position, ComponentSerializer.toString(message));
        }
    }

    @Override
    public void sendActionBar(int units, BaseComponent... message) {
        if (this.getConnectedClient() != null) {
            this.getConnectedClient().getClient().blockPacketUntil(packet -> packet instanceof Chat && ((Chat) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(), System.currentTimeMillis() + (units * 100));
        }
        Constants.EXECUTOR_SERVICE.execute(() -> {
            for (int i = 0; i < units; i++) {
                this.sendMessage(ChatMessageType.ACTION_BAR, message);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void sendData(String channel, byte[] data) {
        sendPacket(new PluginMessage(channel, data, false));
    }

    @Override
    public void useClientSafe(ServiceConnection connection) {
        if (connection == null) {
            this.useClient(null);
            return;
        }
        this.proxy.switchClientSafe(this, connection);
    }

    @Override
    public SocketAddress getSocketAddress() {
        return ch.getRemoteAddress();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getUUID() {
        return getPendingConnection().getUUID();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + message));
    }

    @Override
    public void sendMessage(@NotNull BaseComponent component) {
        this.sendMessage(ChatMessageType.CHAT, component);
    }

    @Override
    public void sendMessage(@NotNull BaseComponent[] component) {
        this.sendMessage(ChatMessageType.CHAT, component);
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return MCProxy.getInstance().getPermissionProvider().hasPermission(this.getUniqueId(), permission);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return getPendingConnection().getUniqueId();
    }

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        header = ChatComponentTransformer.getInstance().transform(this, header)[0];
        footer = ChatComponentTransformer.getInstance().transform(this, footer)[0];

        sendPacket(new PlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        header = ChatComponentTransformer.getInstance().transform(this, header);
        footer = ChatComponentTransformer.getInstance().transform(this, footer);

        sendPacket(new PlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void resetTabHeader() {
        // Mojang did not add a way to remove the header / footer completely, we can only set it to empty
        setTabHeader((BaseComponent) null, null);
    }

    @Override
    public void sendTitle(ProvidedTitle providedTitle) {
        providedTitle.send(this);
    }

    public void setCompressionThreshold(int compressionThreshold) {
        if (!ch.isClosing() && this.compressionThreshold == -1 && compressionThreshold >= 0) {
            this.compressionThreshold = compressionThreshold;
            this.sendPacket(new SetCompression(compressionThreshold));
            ch.setCompressionThreshold(compressionThreshold);
        }
    }

    @Override
    public boolean isConnected() {
        return !ch.isClosed();
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        this.getCh().write(packet);
    }
}
