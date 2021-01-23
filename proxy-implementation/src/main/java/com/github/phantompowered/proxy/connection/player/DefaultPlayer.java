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
package com.github.phantompowered.proxy.connection.player;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.block.BlockStateRegistry;
import com.github.phantompowered.proxy.api.block.half.HorizontalHalf;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.chat.HistoricalMessage;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.entity.EntityStatusType;
import com.github.phantompowered.proxy.api.entity.EntityType;
import com.github.phantompowered.proxy.api.entity.LivingEntityType;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerKickEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerSendProxyMessageEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerServiceSelectedEvent;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.ByteBufUtils;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.OfflinePlayer;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.inventory.PlayerInventory;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.tick.TickHandler;
import com.github.phantompowered.proxy.api.util.LimitedCopyOnWriteArrayList;
import com.github.phantompowered.proxy.connection.AppendedActionBar;
import com.github.phantompowered.proxy.connection.BasicServiceConnection;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.DefaultServiceConnector;
import com.github.phantompowered.proxy.entity.ProxyEntity;
import com.github.phantompowered.proxy.network.channel.WrappedNetworkChannel;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityStatus;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerKickPlayer;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerPluginMessage;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerTitle;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class DefaultPlayer extends ProxyEntity implements Player, WrappedNetworkChannel, TickHandler, Entity.Callable {

    private static final long ONE_TICK_IN_MILLISECONDS = 50;
    private final ServiceRegistry serviceRegistry;
    private final OfflinePlayer offlinePlayer;
    private final long lastLogin = System.currentTimeMillis();
    private final NetworkChannel channel;
    private final int version;
    private final PlayerInventory inventory = new DefaultPlayerInventory(this);
    private final PacketSender.NetworkUnsafe packetSenderUnsafe = new PacketSenderUnsafe();
    private final Collection<AppendedActionBar> actionBars = new CopyOnWriteArrayList<>();
    private boolean firstConnection = true;
    private int entityId;
    private ServiceConnection connectingClient;
    private ServiceConnection connectedClient;
    private boolean connected = false;
    private boolean autoReconnect = true;
    private String lastCommandCompleteRequest;

    private final List<HistoricalMessage> receivedMessages = new LimitedCopyOnWriteArrayList<>(1000); // TODO configurable

    public DefaultPlayer(ServiceRegistry serviceRegistry, ConnectedProxyClient client, OfflinePlayer offlinePlayer, NetworkChannel channel, int version, int compressionThreshold) {
        super(serviceRegistry, client, client.getConnection().getLocation(), client.getEntityId(), LivingEntityType.PLAYER);
        this.serviceRegistry = serviceRegistry;
        this.offlinePlayer = offlinePlayer;

        this.channel = channel;
        this.version = version;

        if (!channel.isClosing() && compressionThreshold >= 0) {
            this.channel.writeWithResult(new PacketLoginOutSetCompression(compressionThreshold)).join();
            channel.setCompression(compressionThreshold);
        }
    }

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

    public String getLastCommandCompleteRequest() {
        return this.lastCommandCompleteRequest;
    }

    public void setLastCommandCompleteRequest(String lastCommandCompleteRequest) {
        this.lastCommandCompleteRequest = lastCommandCompleteRequest;
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public void sendMessage(ChatMessageType position, Component... messages) {
        for (Component message : messages) {
            this.sendMessage(position, message);
        }
    }

    @Override
    public void sendMessage(ChatMessageType position, Component message) {
        this.registry.getProviderUnchecked(EventManager.class).callEvent(new PlayerSendProxyMessageEvent(this, position, message));

        this.getReceivedMessages().add(HistoricalMessage.now(message));

        if (position == ChatMessageType.ACTION_BAR) {
            // the action bar needs to be sent as a legacy text (e.g. "§atest") instead of the new format (e.g. {color:green,text:test} because the client is stupid
            String legacyMessage = LegacyComponentSerializer.legacySection().serialize(message);
            this.sendPacket(new PacketPlayServerChatMessage(GsonComponentSerializer.gson().serialize(Component.text(legacyMessage)), (byte) position.ordinal()));
        } else {
            this.sendPacket(new PacketPlayServerChatMessage(GsonComponentSerializer.gson().serialize(message), (byte) position.ordinal()));
        }
    }

    @Override
    public void sendActionBar(int units, Component... message) {
        if (this.connectedClient != null && this.connectedClient instanceof BasicServiceConnection) {
            ((BasicServiceConnection) this.connectedClient).getClient().blockPacketUntil(packet -> packet instanceof PacketPlayServerChatMessage
                            && ((PacketPlayServerChatMessage) packet).getPosition() == ChatMessageType.ACTION_BAR.ordinal(),
                    System.currentTimeMillis() + (units * 100)
            );
        }

        APIUtil.EXECUTOR_SERVICE.execute(() -> {
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
    public void appendActionBar(@NotNull HorizontalHalf side, @NotNull Supplier<String> message) {
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

        ServiceConnector connector = this.serviceRegistry.getProviderUnchecked(ServiceConnector.class);
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

        this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new PlayerServiceSelectedEvent(this, connection));

        ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeString("vanilla", buf);
        this.sendCustomPayload("MC|Brand", ByteBufUtils.toArray(buf));

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
    public void sendServerMessage(String message) {
        this.connectedClient.chat(message);
    }

    @Override
    public void sendTitle(Title title) {
        PacketPlayServerTitle titlePacket = new PacketPlayServerTitle();
        titlePacket.setAction(PacketPlayServerTitle.Action.TITLE);
        titlePacket.setText(GsonComponentSerializer.gson().serialize(title.title()));
        this.sendPacket(titlePacket);

        PacketPlayServerTitle subTitlePacket = new PacketPlayServerTitle();
        titlePacket.setAction(PacketPlayServerTitle.Action.SUBTITLE);
        titlePacket.setText(GsonComponentSerializer.gson().serialize(title.subtitle()));
        this.sendPacket(subTitlePacket);

        PacketPlayServerTitle timesPacket = new PacketPlayServerTitle();
        timesPacket.setAction(PacketPlayServerTitle.Action.TIMES);
        Title.Times times = title.times();
        if (times != null) {
            timesPacket.setFadeIn((int) (times.fadeIn().toMillis() / ONE_TICK_IN_MILLISECONDS));
            timesPacket.setStay((int) (times.stay().toMillis() / ONE_TICK_IN_MILLISECONDS));
            timesPacket.setFadeOut((int) (times.fadeOut().toMillis() / ONE_TICK_IN_MILLISECONDS));
        }

        this.sendPacket(timesPacket);
    }

    @Override
    public void clearTitle() {
        PacketPlayServerTitle packetPlayServerTitle = new PacketPlayServerTitle();
        packetPlayServerTitle.setAction(PacketPlayServerTitle.Action.CLEAR);
        this.sendPacket(packetPlayServerTitle);
    }

    @Override
    public void resetTitle() {
        PacketPlayServerTitle packetPlayServerTitle = new PacketPlayServerTitle();
        packetPlayServerTitle.setAction(PacketPlayServerTitle.Action.RESET);
        this.sendPacket(packetPlayServerTitle);
    }

    @Override
    public void sendBlockChange(Location pos, int blockState) {
        this.sendPacket(new PacketPlayServerBlockChange(pos, blockState));
    }

    @Override
    public void sendBlockChange(Location pos, Material material) {
        this.sendBlockChange(pos, this.serviceRegistry.getProviderUnchecked(BlockStateRegistry.class).getDefaultBlockState(material));
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    @Override
    public void sendEntityStatus(@Nullable Entity entity, @NotNull EntityStatusType statusType) {
        this.sendPacket(new PacketPlayServerEntityStatus(entity == null ? this.entityId : entity.getEntityId(), statusType.getId()));
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sendMessage(ChatMessageType.CHAT, LegacyComponentSerializer.legacySection().deserialize(APIUtil.MESSAGE_PREFIX + message));
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(APIUtil.MESSAGE_PREFIX);
        this.sendMessage(ChatMessageType.CHAT, prefix.append(component));
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
    public @NotNull InetSocketAddress getSocketAddress() {
        return this.channel.getAddress();
    }

    @Override
    public void disconnect(@NotNull Component reason) {
        this.disconnect0(reason);
    }

    @Override
    public void sendCustomPayload(@NotNull String tag, @NotNull byte[] data) {
        this.sendPacket(new PacketPlayServerPluginMessage(tag, data));
    }

    @Override
    public void sendCustomPayload(@NotNull String tag, @NotNull ProtoBuf data) {
        this.sendCustomPayload(tag, data.toArray());
    }

    @Override
    public List<HistoricalMessage> getReceivedMessages() {
        return this.receivedMessages;
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

    public void handleDisconnected(@NotNull ServiceConnection connection, @NotNull Component reason) {
        if (!this.connected) {
            return;
        }

        this.connected = false;
        if (!this.autoReconnect) {
            this.disconnect(reason);
            return;
        }

        ServiceConnection nextClient = this.serviceRegistry.getProviderUnchecked(ServiceConnector.class).findBestConnection(this.getUniqueId());
        if (nextClient == null || nextClient.equals(connection)) {
            this.disconnect(Component.text(APIUtil.MESSAGE_PREFIX + "Disconnected from " + this.connectedClient.getServerAddress()
                    + ", no fallback client found. Reason:\n§r" + LegacyComponentSerializer.legacySection().serialize(reason)));
            return;
        }

        Component actionBar = LegacyComponentSerializer.legacySection().deserialize(LegacyComponentSerializer.legacySection().serialize(reason).replace('\n', ' '));
        this.sendMessage(ChatMessageType.CHAT, reason);
        this.sendActionBar(200, actionBar);

        Title title = Title.title(
                Component.text("§cDisconnected"),
                Component.empty(),
                Title.Times.of(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))
        );
        this.sendTitle(title);
        this.useClient(nextClient);
    }

    @Override
    public void write(@NotNull Packet packet) {
        this.sendPacket(packet);
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        this.write((Object) packet);
    }

    @Override
    public void write(@NotNull Object packet) {
        ServiceConnection connection = this.connectingClient != null ? this.connectingClient : this.connectedClient;

        if (packet instanceof Packet && connection instanceof BasicServiceConnection && connection.getEntityId() != this.entityId) {
            ((BasicServiceConnection) connection).getEntityRewrite().updatePacketToClient((Packet) packet, connection.getEntityId(), this.entityId);
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
    public boolean isLiving() {
        return true;
    }

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public @Nullable LivingEntityType getLivingType() {
        return LivingEntityType.PLAYER;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.connectedClient != null ? this.connectedClient.getLocation() : Location.ZERO;
    }

    @Override
    public void teleport(@NotNull Location location) {
        if (this.connectedClient != null) {
            this.connectedClient.teleport(location);
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

    public void disconnect0(Component reason) {
        if (this.channel.isClosing()) {
            return;
        }

        PlayerKickEvent event = this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new PlayerKickEvent(this, reason));
        if (event.isCancelled()) {
            return;
        }

        if (event.getReason() != null) {
            reason = event.getReason();
        }

        this.channel.close(new PacketPlayServerKickPlayer(GsonComponentSerializer.gson().serialize(reason)));
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
        return this.lastLogin; // pail - that we can update the offline player properly in the database
    }

    @Override
    public int getLastVersion() {
        return this.version; // pail - that we can update the offline player properly in the database
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
