package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.EntityLiving;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.player.PlayerKickEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.network.channel.WrappedNetworkChannel;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.derrop.proxy.protocol.play.client.PacketPlayChatMessage;
import com.github.derrop.proxy.protocol.play.client.PacketPlayInPositionLook;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerListHeaderFooter;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.tab.TabList;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.UUID;

public class DefaultPlayer extends DefaultOfflinePlayer implements Player, WrappedNetworkChannel {

    public DefaultPlayer(MCProxy proxy, TabList tabList, UUID uniqueId, LoginResult loginResult, NetworkChannel channel, int version, int compressionThreshold) {
        super(uniqueId, loginResult.getName(), System.currentTimeMillis(), version);
        this.proxy = proxy;
        this.tabList = tabList;
        this.displayName = loginResult.getName();

        this.channel = channel;
        this.version = version;

        if (!channel.isClosing() && this.compression == -1 && compressionThreshold >= 0) {
            this.compression = compressionThreshold;
            this.sendPacket(new PacketLoginOutSetCompression(compressionThreshold));
            channel.setCompression(compression);
        }

        this.entityMap = EntityMap.getEntityMap(version);
        MCProxy.getInstance().getUUIDStorage().createMapping(this.getUniqueId(), this.getName());
    }


    private final MCProxy proxy;

    private final TabList tabList;

    private final EntityMap entityMap;
    private final NetworkChannel channel;
    private final int version;

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
            this.connectedClient.getClient().blockPacketUntil(packet -> packet instanceof PacketPlayChatMessage
                            && ((PacketPlayChatMessage) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(),
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
        this.sendPacket(new PacketPlayPluginMessage(channel, data));
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
        if (!this.channel.isConnected()) {
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
    public int getVersion() {
        return this.version;
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
    public void chat(String message) {
        this.connectedClient.chat(message);
    }

    // TODO: replace all tablist method

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        this.sendPacket(new PacketPlayServerPlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        this.sendPacket(new PacketPlayServerPlayerListHeaderFooter(
                ComponentSerializer.toString(header),
                ComponentSerializer.toString(footer)
        ));
    }

    @Override
    public void resetTabHeader() {
        this.setTabHeader(new BaseComponent[0], new BaseComponent[0]);
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
        return this.channel.getAddress();
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
    public NetworkChannel getWrappedNetworkChannel() {
        return this.channel;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
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
        this.channel.write(packet);
    }

    @Override
    public void sendPacket(@NotNull ByteBuf byteBuf) {
        this.channel.write(byteBuf);
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
        this.sendPacket(new PacketPlayChatMessage(message, (byte) position.ordinal()));
    }

    public void disconnect0(BaseComponent... reason) {
        if (this.channel.isClosing()) {
            return;
        }

        PlayerKickEvent event = this.proxy.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerKickEvent(this, reason));
        if (event.isCancelled()) {
            return;
        }

        if (event.getReason() != null) {
            reason = event.getReason();
        }

        this.channel.close(new PacketPlayServerKickPlayer(ComponentSerializer.toString(reason)));
        if (this.connectedClient != null) {
            this.connectedClient.getClient().free();
        }
    }

    private void handleLocationUpdate() {
        this.sendPacket(new PacketPlayServerEntityTeleport(this.getEntityId(), this.location, this.isOnGround()));
        this.connectedClient.sendPacket(new PacketPlayInPositionLook(this.location, Collections.emptySet()));
    }

    public EntityMap getEntityMap() {
        return entityMap;
    }

    private class Unsafe0 implements EntityLiving.Unsafe {

        @Override
        public void setLocationUnchecked(@NotNull Location locationUnchecked) {
            DefaultPlayer.this.location = locationUnchecked;
        }
    }

    private class PacketSenderUnsafe implements PacketSender.NetworkUnsafe {

        @Override
        public void sendPacket(@NotNull Object packet) {
            DefaultPlayer.this.channel.write(packet);
            DefaultPlayer.this.handleLocationUpdate();
        }
    }
}
