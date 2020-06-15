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
package com.github.derrop.proxy.connection.player;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.Tickable;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.connection.player.OfflinePlayer;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.connection.player.inventory.PlayerInventory;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.entity.LivingEntityType;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.player.PlayerKickEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerServiceSelectedEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.api.util.Side;
import com.github.derrop.proxy.connection.*;
import com.github.derrop.proxy.entity.ProxyEntity;
import com.github.derrop.proxy.network.channel.WrappedNetworkChannel;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerPlayerListHeaderFooter;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerPluginMessage;
import com.github.derrop.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import io.netty.buffer.ByteBuf;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class DefaultPlayer extends ProxyEntity implements Player, WrappedNetworkChannel, Tickable, Entity.Callable {

    public DefaultPlayer(MCProxy proxy, ConnectedProxyClient client, OfflinePlayer offlinePlayer, LoginResult loginResult, NetworkChannel channel, int version, int compressionThreshold) {
        super(proxy.getServiceRegistry(), client, client.getConnection().getLocation(), client.getEntityId(), LivingEntityType.PLAYER.getTypeId());
        this.proxy = proxy;
        this.offlinePlayer = offlinePlayer;
        this.displayName = loginResult.getName();

        this.channel = channel;
        this.version = version;

        if (!channel.isClosing() && compressionThreshold >= 0) {
            this.channel.writeWithResult(new PacketLoginOutSetCompression(compressionThreshold)).join();
            channel.setCompression(compressionThreshold);
        }
    }

    private final MCProxy proxy;
    private final OfflinePlayer offlinePlayer;
    private final NetworkChannel channel;
    private final int version;
    private boolean firstConnection = true;
    private int entityId;
    private ServiceConnection connectingClient;
    private ServiceConnection connectedClient;
    private boolean connected = false;
    private boolean autoReconnect = true;
    private String displayName;
    private String lastCommandCompleteRequest;
    private final PlayerInventory inventory = new DefaultPlayerInventory(this);
    private final PacketSender.NetworkUnsafe packetSenderUnsafe = new PacketSenderUnsafe();
    private final Collection<AppendedActionBar> actionBars = new CopyOnWriteArrayList<>();

    public void applyPermissions(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == this) {
            return;
        }

        this.clearPermissions();
        this.getEffectivePermissions().putAll(offlinePlayer.getEffectivePermissions());
    }

    public ServiceConnection getConnectingClient() {
        return this.connectingClient;
    }

    public Collection<AppendedActionBar> getActionBars() {
        return this.actionBars;
    }

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

    public String getLastCommandCompleteRequest() {
        return this.lastCommandCompleteRequest;
    }

    public void setLastCommandCompleteRequest(String lastCommandCompleteRequest) {
        this.lastCommandCompleteRequest = lastCommandCompleteRequest;
    }

    @Override
    public void sendMessage(ChatMessageType position, Component... messages) {
        for (Component message : messages) {
            this.sendMessage(position, message);
        }
    }

    @Override
    public void sendMessage(ChatMessageType position, Component message) {
        this.sendMessage(position, GsonComponentSerializer.INSTANCE.serialize(message));
    }

    @Override
    public void sendActionBar(int units, Component... message) {
        if (this.connectedClient != null && this.connectedClient instanceof BasicServiceConnection) {
            ((BasicServiceConnection) this.connectedClient).getClient().blockPacketUntil(packet -> packet instanceof PacketPlayServerChatMessage
                            && ((PacketPlayServerChatMessage) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(),
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
    public void appendActionBar(@NotNull Side side, @NotNull Supplier<String> message) {
        this.actionBars.add(new AppendedActionBar(side, message));
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.sendPacket(new PacketPlayServerPluginMessage(channel, data));
    }

    @Override
    public void useClientSafe(ServiceConnection connection) {
        if (connection == null) {
            this.useClient(null);
            return;
        }

        ServiceConnector connector = this.proxy.getServiceRegistry().getProviderUnchecked(ServiceConnector.class);
        if (connector instanceof DefaultServiceConnector) {
            ((DefaultServiceConnector) connector).switchClientSafe(this, connection);
        } else {
            this.useClient(connection);
        }
    }

    @Override
    public void useClient(ServiceConnection connection) {
        if (!this.channel.isConnected()) {
            return;
        }

        if (connection == null) {
            if (this.connectedClient != null && this.connectedClient instanceof BasicServiceConnection) {
                ((BasicServiceConnection) this.connectedClient).getClient().free();
                this.connectedClient = null;
            }

            return;
        }

        if (this.connectedClient != null && this.connectedClient.getCredentials().equals(connection.getCredentials())) {
            this.sendMessage("Already connected with this client");
            return;
        }

        if (this.firstConnection) {
            this.firstConnection = false;
            this.entityId = connection.getEntityId();
        }

        if (this.connectedClient != null && this.connectedClient instanceof BasicServiceConnection) {
            ((BasicServiceConnection) this.connectedClient).getClient().free();
        }

        this.connected = true;

        this.connectingClient = connection;

        connection.syncPackets(this, this.connectedClient != null);
        this.connectedClient = connection;

        this.connectingClient = null;

        this.proxy.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerServiceSelectedEvent(this, connection));

        //this.sendMessage("§7Your name: §e" + connection.getName());
    }

    @Override
    public ServiceConnection getConnectedClient() {
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
    public void setTabHeader(Component header, Component footer) {
        this.sendPacket(new PacketPlayServerPlayerListHeaderFooter(
                GsonComponentSerializer.INSTANCE.serialize(header),
                GsonComponentSerializer.INSTANCE.serialize(footer)
        ));
    }

    @Override
    public void resetTabHeader() {
        this.setTabHeader(TextComponent.empty(), TextComponent.empty());
    }

    @Override
    public void sendTitle(ProvidedTitle providedTitle) {
        providedTitle.send(this);
    }

    @Override
    public void sendBlockChange(Location pos, int blockState) {
        this.sendPacket(new PacketPlayServerBlockChange(pos, blockState));
    }

    @Override
    public void sendBlockChange(Location pos, Material material) {
        this.sendBlockChange(pos, this.proxy.getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class).getDefaultBlockState(material));
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sendMessage(ChatMessageType.CHAT, LegacyComponentSerializer.legacyLinking().deserialize(Constants.MESSAGE_PREFIX + message));
    }

    @Override
    public void sendMessage(@NotNull Component component) {
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
    public void disconnect(@NotNull Component reason) {
        this.disconnect0(reason);
    }

    @Override
    public NetworkChannel getWrappedNetworkChannel() {
        return this.channel;
    }

    @Override
    public boolean isConnected() {
        return this.connected && this.channel.isConnected();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void handleDisconnected(@NotNull ServiceConnection connection, @NotNull Component reason) {
        if (!this.connected) {
            return;
        }

        this.connected = false;
        if (!this.autoReconnect) {
            this.disconnect(reason);
            return;
        }

        ServiceConnection nextClient = this.proxy.getServiceRegistry().getProviderUnchecked(ServiceConnector.class).findBestConnection(this.getUniqueId());
        if (nextClient == null || nextClient.equals(connection)) {
            this.disconnect(Constants.MESSAGE_PREFIX + "Disconnected from " + this.connectedClient.getServerAddress()
                    + ", no fallback client found. Reason:\n§r" + LegacyComponentSerializer.legacy().serialize(reason));
            return;
        }

        Component actionBar = LegacyComponentSerializer.legacyLinking().deserialize(LegacyComponentSerializer.legacy().serialize(reason).replace('\n', ' '));
        this.sendMessage(ChatMessageType.CHAT, reason);
        this.sendActionBar(200, actionBar);

        ProvidedTitle title = this.proxy
                .createTitle()
                .title("§cDisconnected")
                .fadeIn(20)
                .stay(100)
                .fadeOut(20);
        this.sendTitle(title);
        this.useClient(nextClient);
    }

    @Override
    public void write(@NotNull Packet packet) {
        this.sendPacket(packet);
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        ServiceConnection connection = this.connectingClient != null ? this.connectingClient : this.connectedClient;
        if (connection instanceof BasicServiceConnection) {
            ((BasicServiceConnection) connection).getEntityRewrite().updatePacketToClient(packet, connection.getEntityId(), this.entityId);
        }
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
    public int getType() {
        return LivingEntityType.PLAYER.getTypeId();
    }

    @Override
    public @NotNull Location getLocation() {
        return this.connectedClient != null ? this.connectedClient.getLocation() : Location.ZERO;
    }

    @Override
    public void setLocation(@NotNull Location location) {
        if (this.connectedClient != null) {
            this.connectedClient.setLocation(location);
        }
    }

    @Override
    public boolean isOnGround() {
        return this.connectedClient != null && this.connectedClient.isOnGround();
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public int getDimension() {
        return this.connectedClient == null ? -1 : this.connectedClient.getDimension();
    }

    @Override
    public @NotNull Callable getCallable() {
        return this;
    }

    @Override
    public float getHeadHeight() {
        return 1.8F;
    }

    private void sendMessage(@NotNull ChatMessageType position, @NotNull String message) {
        this.sendPacket(new PacketPlayServerChatMessage(message, (byte) position.ordinal()));
    }

    public void disconnect0(Component reason) {
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

        this.channel.close(new PacketPlayServerKickPlayer(GsonComponentSerializer.INSTANCE.serialize(reason)));
        if (this.connectedClient != null && this.connectedClient instanceof BasicServiceConnection) {
            ((BasicServiceConnection) this.connectedClient).getClient().free();
        }
    }

    @Override
    public void handleTick() {
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.offlinePlayer.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return this.offlinePlayer.getName();
    }

    @Override
    public long getLastLogin() {
        return this.offlinePlayer.getLastLogin();
    }

    @Override
    public int getLastVersion() {
        return this.offlinePlayer.getLastVersion();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.offlinePlayer.hasPermission(permission);
    }

    @Override
    public void addPermission(@NotNull String permission, boolean set) {
        this.offlinePlayer.addPermission(permission, set);
    }

    @Override
    public void removePermission(@NotNull String permission) {
        this.offlinePlayer.removePermission(permission);
    }

    @Override
    public void clearPermissions() {
        this.offlinePlayer.clearPermissions();
    }

    @Override
    public @NotNull Map<String, Boolean> getEffectivePermissions() {
        return this.offlinePlayer.getEffectivePermissions();
    }

    private class PacketSenderUnsafe implements PacketSender.NetworkUnsafe {

        @Override
        public void sendPacket(@NotNull Object packet) {
            DefaultPlayer.this.channel.write(packet);
        }
    }
}
