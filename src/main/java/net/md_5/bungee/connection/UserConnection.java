package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.PlayerUniqueTabList;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.ChatComponentTransformer;
import net.md_5.bungee.api.BungeeTitle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.*;
import net.md_5.bungee.tab.TabList;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
public final class UserConnection implements ProxiedPlayer {

    /*========================================================================*/
    @NonNull
    private final ChannelWrapper ch;
    @Getter
    @NonNull
    private final String name;
    @Getter
    private final InitialHandler pendingConnection;
    /*========================================================================*/
    @Getter
    @Setter
    private ConnectedProxyClient proxyClient;
    @Getter
    @Setter
    private int dimension;
    @Getter
    @Setter
    private boolean dimensionChange = true;
    /*========================================================================*/
    @Getter
    private TabList tabListHandler;
    /*========================================================================*/
    @Getter
    private final Scoreboard serverSentScoreboard = new Scoreboard();
    @Getter
    private final Collection<UUID> sentBossBars = new HashSet<>();
    private boolean autoReconnect = true;
    /*========================================================================*/
    @Getter
    private String displayName;
    @Getter
    private EntityMap entityRewrite;
    private int compressionThreshold = -1;
    private boolean connected = false;
    /*========================================================================*/
    private final Unsafe unsafe = new Unsafe() {
        @Override
        public void sendPacket(DefinedPacket packet) {
            ch.write(packet);
        }
    };

    public ChannelWrapper getCh() {
        return ch;
    }

    public void init() {
        this.entityRewrite = EntityMap.getEntityMap(getPendingConnection().getVersion());

        this.displayName = name;

        tabListHandler = new PlayerUniqueTabList(this.ch);

        MCProxy.getInstance().getUUIDStorage().createMapping(this.getUniqueId(), this.getName());
    }

    public int getClientEntityId() {
        return this.proxyClient == null ? 0 : this.proxyClient.getEntityId();
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
    public void setDisplayName(String name) {
        Preconditions.checkNotNull(name, "displayName");
        displayName = name;
    }

    public void handleDisconnected(ConnectedProxyClient proxyClient, BaseComponent[] reason) {
        if (!this.connected) {
            return;
        }
        this.connected = false;

        if (!this.autoReconnect) {
            this.disconnect(reason);
            return;
        }

        ConnectedProxyClient nextClient = MCProxy.getInstance().findBestProxyClient(this.getUniqueId());
        if (nextClient == null || nextClient.equals(proxyClient)) {
            this.disconnect("No client found:\n" + TextComponent.toLegacyText(reason));
            return;
        }
        if (reason != null) {
            nextClient.blockPacketUntil(packet -> packet instanceof Chat && ((Chat) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(), System.currentTimeMillis() + 10000);

            this.sendMessage(ChatMessageType.CHAT, reason);
            new Thread(() -> {
                for (int i = 0; i < 200; i++) {
                    this.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(TextComponent.toPlainText(reason).replace('\n', ' ')));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }).start();
        }

        this.sendTitle(new BungeeTitle().title(TextComponent.fromLegacyText("§cDisconnected")).fadeIn(20).stay(100).fadeOut(20));

        this.useClient(nextClient);

        //this.disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "§cDisconnected!\n§7There are §e" + MCProxy.getInstance().getFreeClients() + " free clients §7left!"));
    }

    @Override
    public void useClient(ConnectedProxyClient proxyClient) {
        if (proxyClient == null) {
            if (this.proxyClient != null) {
                this.proxyClient.free();
                this.proxyClient.getScoreboard().writeClear(this);
            }
            return;
        }

        if (this.proxyClient != null && this.proxyClient.getCredentials().equals(proxyClient.getCredentials())) {
            this.sendMessage("already connected with this client");
            return;
        }

        if (this.proxyClient != null) {
            this.proxyClient.free();
            this.proxyClient.getScoreboard().writeClear(this);
        }

        for (UUID bossbarId : this.sentBossBars) {
            // Send remove bossbar packet
            this.unsafe.sendPacket(new net.md_5.bungee.protocol.packet.BossBar(bossbarId, 1));
        }
        this.sentBossBars.clear();

        this.tabListHandler.onServerChange();

        this.connected = true;

        proxyClient.redirectPackets(this, this.proxyClient != null);
        proxyClient.getScoreboard().write(this);

        this.sendMessage("§7Your name: §e" + proxyClient.getAccountName());

        this.proxyClient = proxyClient;
    }

    @Override
    public ConnectedProxyClient getConnectedClient() {
        return this.proxyClient;
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

    public void disconnect0(final BaseComponent... reason) {
        if (!ch.isClosing()) {
            ch.close(new Kick(ComponentSerializer.toString(reason)));

            if (this.proxyClient != null) {
                this.proxyClient.free();
            }
        }
    }

    @Override
    public void chat(String message) {
        Preconditions.checkState(proxyClient != null, "Not connected to server");
        this.proxyClient.getChannelWrapper().write(new Chat(message));
    }

    @Deprecated
    private void sendMessage(ChatMessageType position, String message) {
        unsafe().sendPacket(new Chat(message, (byte) position.ordinal()));
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
    public void sendData(String channel, byte[] data) {
        unsafe().sendPacket(new PluginMessage(channel, data, false));
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getSocketAddress();
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
    public Unsafe unsafe() {
        return unsafe;
    }

    @Override
    public String getUUID() {
        return getPendingConnection().getUUID();
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return MCProxy.getInstance().getPermissionProvider().hasPermission(this.getUniqueId(), permission);
    }

    @Override
    public UUID getUniqueId() {
        return getPendingConnection().getUniqueId();
    }

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        header = ChatComponentTransformer.getInstance().transform(this, header)[0];
        footer = ChatComponentTransformer.getInstance().transform(this, footer)[0];

        unsafe().sendPacket(new PlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        header = ChatComponentTransformer.getInstance().transform(this, header);
        footer = ChatComponentTransformer.getInstance().transform(this, footer);

        unsafe().sendPacket(new PlayerListHeaderFooter(
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
    public void sendTitle(Title title) {
        title.send(this);
    }

    public String getExtraDataInHandshake() {
        return this.getPendingConnection().getExtraDataInHandshake();
    }

    public void setCompressionThreshold(int compressionThreshold) {
        if (!ch.isClosing() && this.compressionThreshold == -1 && compressionThreshold >= 0) {
            this.compressionThreshold = compressionThreshold;
            unsafe.sendPacket(new SetCompression(compressionThreshold));
            ch.setCompressionThreshold(compressionThreshold);
        }
    }

    @Override
    public boolean isConnected() {
        return !ch.isClosed();
    }

    @Override
    public Scoreboard getScoreboard() {
        return serverSentScoreboard;
    }
}
