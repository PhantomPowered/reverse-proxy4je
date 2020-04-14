package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.packet.Packet;
import com.github.derrop.proxy.api.entity.EntityLiving;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.player.PlayerKickEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.util.ChatMessageType;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.protocol.client.PacketC06PlayerPosLook;
import com.github.derrop.proxy.protocol.server.PacketPlayOutEntityTeleport;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.*;
import net.md_5.bungee.tab.TabList;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.Collections;

public class DefaultPlayer extends DefaultOfflinePlayer implements Player {

    public DefaultPlayer(MCProxy proxy, TabList tabList, ChannelWrapper channelWrapper, InitialHandler initialHandler, int compressionThreshold) {
        super(initialHandler.getUniqueId(), initialHandler.getName(), System.currentTimeMillis());
        this.proxy = proxy;
        this.tabList = tabList;
        this.channelWrapper = channelWrapper;
        this.initialHandler = initialHandler;
        this.displayName = initialHandler.getName();

        if (!channelWrapper.isClosing() && this.compression == -1 && compressionThreshold >= 0) {
            this.compression = compressionThreshold;
            this.sendPacket(new SetCompression(compressionThreshold));
            channelWrapper.setCompressionThreshold(compressionThreshold);
        }

        this.entityMap = EntityMap.getEntityMap(initialHandler.getVersion());
        MCProxy.getInstance().getUUIDStorage().createMapping(this.getUniqueId(), this.getName());
    }

    private final EntityMap entityMap;

    private final MCProxy proxy;

    private final TabList tabList;

    private final ChannelWrapper channelWrapper; // TODO: replace with another class

    private final InitialHandler initialHandler; // TODO: replace this with another class

    private BasicServiceConnection connectedClient;

    private Location location;

    private boolean connected = false;

    private boolean autoReconnect = true;

    private int dimension;

    private int compression = -1;

    private boolean dimensionChange;

    private String displayName;

    private final EntityLiving.Unsafe unsafe = new Unsafe0();

    private final PacketSender.NetworkUnsafe packetSenderUnsafe = new PacketSenderUnsafe();

    @Override
    public Proxy getProxy() {
        return this.proxy;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent... message) {
        if (position == ChatMessageType.ACTION_BAR) {
            this.sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
        } else {
            this.sendMessage(position, ComponentSerializer.toString(message));
        }
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent message) {
        if (position == ChatMessageType.ACTION_BAR) {
            this.sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
        } else {
            this.sendMessage(position, ComponentSerializer.toString(message));
        }
    }

    @Override
    public void sendActionBar(int units, BaseComponent... message) {
        if (this.connectedClient != null) {
            this.connectedClient.getClient().blockPacketUntil(packet -> packet instanceof Chat
                            && ((Chat) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(),
                    System.currentTimeMillis() + (units * 100)
            );
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
        this.sendPacket(new PluginMessage(channel, data, false));
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
    public void useClient(ServiceConnection connection) {
        if (this.channelWrapper.isClosing() || this.channelWrapper.isClosed()) {
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
            this.sendMessage("Already connected with this client");
            return;
        }

        if (this.connectedClient != null) {
            this.connectedClient.getClient().free();
        }

        this.tabList.onServerChange();
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
    public InitialHandler getPendingConnection() {
        return this.initialHandler;
    }

    @Override
    public void chat(String message) {
        this.connectedClient.sendPacket(new Chat(message));
    }

    // TODO: replace all tablist method

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        this.sendPacket(new PlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        this.sendPacket(new PlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void resetTabHeader() {
    }

    @Override
    public void sendTitle(ProvidedTitle providedTitle) {
        providedTitle.send(this);
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
    public void sendMessages(@NotNull String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return this.hasPermission(permission);
    }

    @Override
    public @NotNull SocketAddress getSocketAddress() {
        return this.initialHandler.getSocketAddress();
    }

    @Override
    public void disconnect(@NotNull String reason) {
        this.disconnect(TextComponent.fromLegacyText(reason));
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        this.disconnect0(reason);
    }

    @Override
    public void disconnect(@NotNull BaseComponent reason) {
        this.disconnect0(reason);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void handleDisconnected(@NotNull ServiceConnection connection, @NotNull BaseComponent[] reason) {
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
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        this.channelWrapper.write(packet);
    }

    @Override
    public void sendPacket(@NotNull ByteBuf byteBuf) {
        this.channelWrapper.write(byteBuf);
    }

    @Override
    public @NotNull NetworkUnsafe networkUnsafe() {
        return this.packetSenderUnsafe;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(@NotNull Location location) {
        if (this.location != null && this.location.distance(location) > 2) {
            this.unsafe().setLocationUnchecked(location);
        }
    }

    @Override
    public boolean isOnGround() {
        return this.connectedClient != null && this.connectedClient.isOnGround();
    }

    @Override
    public int getEntityId() {
        return this.connectedClient == null ? 0 : this.connectedClient.getEntityId();
    }

    @Override
    public int getDimension() {
        return this.dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public boolean isDimensionChange() {
        return this.dimensionChange;
    }

    @Override
    public void setDimensionChange(boolean dimensionChange) {
        this.dimensionChange = dimensionChange;
    }

    @Override
    public @NotNull EntityLiving.Unsafe unsafe() {
        return this.unsafe;
    }

    private void sendMessage(@NotNull ChatMessageType position, @NotNull String message) {
        this.sendPacket(new Chat(message, (byte) position.ordinal()));
    }

    public void disconnect0(BaseComponent... reason) {
        if (channelWrapper.isClosing()) {
            return;
        }

        PlayerKickEvent event = this.proxy.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerKickEvent(this, reason));
        if (event.isCancelled()) {
            return;
        }

        if (event.getReason() != null) {
            reason = event.getReason();
        }

        channelWrapper.close(new Kick(ComponentSerializer.toString(reason)));
        if (this.connectedClient != null) {
            this.connectedClient.getClient().free();
        }
    }

    @Deprecated
    public ChannelWrapper getChannelWrapper() {
        return channelWrapper;
    }

    private void handleLocationUpdate() {
        this.sendPacket(new PacketPlayOutEntityTeleport(this.getEntityId(), this.location, this.isOnGround()));
        this.connectedClient.sendPacket(new PacketC06PlayerPosLook(this));
    }

    public EntityMap getEntityMap() {
        return entityMap;
    }

    private class Unsafe0 implements EntityLiving.Unsafe {

        @Override
        public void setLocationUnchecked(@NotNull Location locationUnchecked) {
            DefaultPlayer.this.location = locationUnchecked;
            DefaultPlayer.this.handleLocationUpdate();
        }
    }

    private class PacketSenderUnsafe implements PacketSender.NetworkUnsafe {

        @Override
        public void sendPacket(@NotNull Object packet) {
            DefaultPlayer.this.channelWrapper.write(packet);
        }
    }
}
