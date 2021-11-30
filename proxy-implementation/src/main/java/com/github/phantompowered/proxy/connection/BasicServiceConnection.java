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
package com.github.phantompowered.proxy.connection;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.block.BlockAccess;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.chat.HistoricalMessage;
import com.github.phantompowered.proxy.api.connection.DefaultConnectionHandler;
import com.github.phantompowered.proxy.api.connection.ServiceConnectResult;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.connection.ServiceInventory;
import com.github.phantompowered.proxy.api.connection.ServiceWorldDataProvider;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.service.TabListUpdateEvent;
import com.github.phantompowered.proxy.api.location.BlockingObject;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.PlayerAbilities;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.scoreboard.Scoreboard;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.session.ProvidedSessionService;
import com.github.phantompowered.proxy.api.task.DefaultTask;
import com.github.phantompowered.proxy.api.task.Task;
import com.github.phantompowered.proxy.api.util.LimitedCopyOnWriteArrayList;
import com.github.phantompowered.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.phantompowered.proxy.connection.player.DefaultPlayerAbilities;
import com.github.phantompowered.proxy.network.channel.WrappedNetworkChannel;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientChatMessage;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientCustomPayload;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerPlayerListHeaderFooter;
import com.github.phantompowered.proxy.protocol.rewrite.EntityRewrite;
import com.github.phantompowered.proxy.protocol.rewrite.EntityRewrites;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class BasicServiceConnection extends BasicInteractiveServiceConnection implements ServiceConnection, WrappedNetworkChannel, Entity.Callable {

    private final ServiceRegistry serviceRegistry;
    private final MCServiceCredentials credentials;
    private final UserAuthentication authentication;
    private final NetworkAddress networkAddress;
    private final EntityRewrite entityRewrite;
    private final ServiceWorldDataProvider worldDataProvider = new BasicServiceWorldDataProvider(this);
    private final PlayerAbilities abilities = new DefaultPlayerAbilities(this);
    private final ServiceInventory inventory = new DefaultServiceInventory(this);
    private ConnectedProxyClient client;
    private boolean sneaking;
    private boolean sprinting;
    private Location location = new Location(0, 0, 0, 0, 0);
    private Component tabHeader = Component.empty();
    private Component tabFooter = Component.empty();

    private long lastConnectionTryTimestamp = -1;

    private final List<HistoricalMessage> receivedMessages = new LimitedCopyOnWriteArrayList<>(1000); // TODO configurable

    public BasicServiceConnection(ServiceRegistry serviceRegistry, MCServiceCredentials credentials, NetworkAddress networkAddress, int version) throws AuthenticationException {
        this(serviceRegistry, credentials, null, networkAddress, version);
    }

    public BasicServiceConnection(ServiceRegistry serviceRegistry, MCServiceCredentials credentials, UserAuthentication authentication, NetworkAddress networkAddress, int version) throws AuthenticationException {
        this.serviceRegistry = serviceRegistry;
        this.credentials = credentials;
        this.networkAddress = networkAddress;

        EntityRewrite entityRewrite = EntityRewrites.getRewrite(version);
        if (entityRewrite == null) {
            throw new IllegalStateException("Unknown version: " + version);
        }
        this.entityRewrite = entityRewrite;

        if (authentication == null && !credentials.isOffline()) {
            System.out.println("Logging in " + credentials.getEmail() + "...");
            authentication = this.serviceRegistry.getProviderUnchecked(ProvidedSessionService.class).login(credentials.getEmail(), credentials.getPassword());
            System.out.println("Successfully logged in with " + credentials.getEmail() + "!");
        }

        this.authentication = authentication;
    }

    private void handleLocationUpdate(Location newLocation) {
        Packet clientPacket = PacketPlayClientPlayerPosition.create(this.location, newLocation);
        if (clientPacket == null) {
            return;
        }
        if (this.getPlayer() != null) {
            this.getPlayer().sendPacket(new PacketPlayServerEntityTeleport(this.getEntityId(), this.location));
        }
        this.client.write(clientPacket);

        this.location = newLocation;
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.client == null ? null : this.client.getRedirector();
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }

    @Override
    public @NotNull Location getHeadLocation() {
        return this.location.clone().add(0, this.isSneaking() ? 1.54F : 1.62F, 0);
    }

    @ApiStatus.Internal
    public void setLocation(Location location) {
        this.handleLocationUpdate(location);
        this.location = location;
    }

    @Override
    public int getDimension() {
        return this.client.getDimension();
    }

    @Override
    public long getLastDisconnectionTimestamp() {
        return this.client.getLastDisconnectionTimestamp();
    }

    @Override
    public long getLastConnectionTryTimestamp() {
        return this.lastConnectionTryTimestamp;
    }

    @Override
    public PlayerId getLastConnectedPlayer() {
        return this.client.getLastConnectedPlayer();
    }

    @Override
    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    @Override
    public @NotNull MCServiceCredentials getCredentials() {
        return this.credentials;
    }

    @Override
    public @Nullable UserAuthentication getAuthentication() {
        return this.client.getAuthentication();
    }

    public EntityRewrite getEntityRewrite() {
        return this.entityRewrite;
    }

    @Override
    public UUID getUniqueId() {
        return this.credentials.isOffline() ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.credentials.getUsername()).getBytes()) : this.authentication.getSelectedProfile().getId();
    }

    @Override
    public String getName() {
        return this.credentials.isOffline() ? this.credentials.getUsername() : this.authentication.getSelectedProfile().getName();
    }

    @Override
    public int getEntityId() {
        return this.client.getEntityId();
    }

    @Override
    public void updateLocation(@NotNull Location location) {
        this.location = location;
    }

    @Override
    public Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(this.client.getViewers());
    }

    @Override
    public void startViewing(Player player) {
        if (this.client.getViewers().contains(player)) {
            return;
        }
        this.client.addViewer(player);
    }

    @Override
    public void stopViewing(Player player) {
        this.client.getViewers().remove(player);
    }

    @Override
    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    @Override
    public boolean isSprinting() {
        return this.sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    @Override
    public ServiceInventory getInventory() {
        return this.inventory;
    }

    @Override
    public Location getTargetBlock(int range) {
        return this.getTargetBlock(null, range);
    }

    @Override
    public Location getTargetBlock(Set<Material> transparent, int range) {
        return this.getTargetObject(range, location -> {
            Material material = this.getBlockAccess().getMaterial(location);

            if ((transparent == null && material != Material.AIR) || (transparent != null && !transparent.contains(material))) {
                return BlockingObject.block(location);
            }

            return null;
        }).getLocation();
    }

    @Override
    public @NotNull BlockingObject getTargetObject(int range) {
        return this.getTargetObject(range, location -> {
            Material material = this.getBlockAccess().getMaterial(location);
            if (material != Material.AIR) {
                return BlockingObject.block(location);
            }

            for (Entity entity : this.worldDataProvider.getEntitiesInWorld()) {
                if (entity.getBoundingBox().contains(location)) {
                    return BlockingObject.entity(entity, location);
                }
            }

            return null;
        });
    }

    private BlockingObject getTargetObject(int range, Function<Location, BlockingObject> function) {
        Location startLocation = this.getHeadLocation();
        Vector direction = startLocation.getDirection().divide(new Vector(10, 10, 10));

        int rangeSq = range * range;

        Location currentLocation = startLocation.clone();

        while (startLocation.distanceSquared(currentLocation) <= rangeSq) {
            currentLocation.add(direction.getX(), direction.getY(), direction.getZ());

            BlockingObject object = function.apply(currentLocation);
            if (object != null) {
                return object;
            }
        }

        return BlockingObject.miss();
    }

    @Override
    public boolean isOnGround() {
        return this.location != null && this.location.isOnGround();
    }

    @Override
    public ServiceWorldDataProvider getWorldDataProvider() {
        return this.worldDataProvider;
    }

    public ConnectedProxyClient getClient() {
        return this.client;
    }

    public void sendPacket(Object packet) {
        this.client.write(packet);
    }

    @Override
    public @NotNull NetworkAddress getServerAddress() {
        return this.networkAddress;
    }

    @Override
    public void chat(@NotNull String message) {
        this.client.write(new PacketPlayClientChatMessage(message));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull String message) {
        this.client.write(new PacketPlayServerChatMessage(message, (byte) type.ordinal()));
    }

    @Override
    public void chat(@NotNull Component component) {
        this.chat(GsonComponentSerializer.gson().serialize(component));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull Component component) {
        this.displayMessage(type, GsonComponentSerializer.gson().serialize(component));
    }

    @Override
    public void chat(@NotNull Component... components) {
        for (Component component : components) {
            this.chat(component);
        }
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull Component... components) {
        for (Component component : components) {
            this.displayMessage(type, component);
        }
    }

    @Override
    public boolean setTabHeaderAndFooter(Component header, Component footer) {
        TabListUpdateEvent event = this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new TabListUpdateEvent(
                this,
                header,
                footer
        ));
        if (event.isCancelled()) {
            return true;
        }

        this.tabHeader = event.getHeader() == null ? Component.empty() : event.getHeader();
        this.tabFooter = event.getFooter() == null ? Component.empty() : event.getFooter();

        Player player = this.getPlayer();
        if (player != null) {
            player.sendPacket(new PacketPlayServerPlayerListHeaderFooter(
                    GsonComponentSerializer.gson().serialize(this.tabHeader),
                    GsonComponentSerializer.gson().serialize(this.tabFooter)
            ));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void resetTabHeaderAndFooter() {
        this.setTabHeaderAndFooter(null, null);
    }

    @Override
    public Component getTabHeader() {
        return this.tabHeader;
    }

    @Override
    public Component getTabFooter() {
        return this.tabFooter;
    }

    @Override
    public @NotNull Task<ServiceConnectResult> connect() {
        if (this.isConnected()) {
            throw new IllegalStateException("Cannot connect if this client is already connected");
        }

        Task<ServiceConnectResult> task = new DefaultTask<>();

        task.exceptionally(throwable -> {
            if (this.client != null) {
                this.client.close();
                this.client = null;
            }
            return null;
        });

        task.addListener(DefaultConnectionHandler.console(this));

        APIUtil.EXECUTOR_SERVICE.execute(() -> {
            this.client = new ConnectedProxyClient(this.serviceRegistry, this);
            this.client.setAuthentication(this.authentication, this.credentials);
            this.client.connect(this.networkAddress, null, task);

            this.lastConnectionTryTimestamp = System.currentTimeMillis();
        });

        return task;
    }

    @Override
    public @NotNull InetSocketAddress getSocketAddress() {
        return (InetSocketAddress) this.client.getWrappedChannel().remoteAddress();
    }

    @Override
    public void disconnect(@NotNull Component reason) {
        this.close();
    }

    @Override
    public void sendCustomPayload(@NotNull String tag, @NotNull byte[] data) {
        this.sendPacket(new PacketPlayClientCustomPayload(tag, data));
    }

    @Override
    public void sendCustomPayload(@NotNull String tag, @NotNull ProtoBuf data) {
        this.sendCustomPayload(tag, data.toArray());
    }

    @Override
    public int getPing() {
        UUID uniqueId = this.getUniqueId();
        for (PacketPlayServerPlayerInfo.Item item : this.getClient().getPacketCache().getHandler(PlayerInfoCache.class).getItems()) {
            if (item.getUniqueId().equals(uniqueId)) {
                return item.getPing();
            }
        }
        return -1;
    }

    @Override
    public List<HistoricalMessage> getReceivedMessages() {
        return this.receivedMessages;
    }

    @Override
    public NetworkChannel getWrappedNetworkChannel() {
        return this.client;
    }

    @Override
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    @Override
    public void unregister() {
        this.close();
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.client.getScoreboard();
    }

    @Override
    public BlockAccess getBlockAccess() {
        return this.client.getPacketCache().getBlockAccess();
    }

    @Override
    public void syncPackets(Player player, boolean switched) {
        this.client.redirectPackets(player, switched);
    }

    @Override
    public void close() {
        if (!this.isConnected()) {
            return;
        }

        this.client.disconnect();
        this.serviceRegistry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().remove(this);
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        if (this.client == null) {
            return;
        }

        this.client.write(packet);
    }

    @Override
    public void sendPacket(@NotNull ByteBuf byteBuf) {
        if (this.client == null) {
            return;
        }

        this.client.write(byteBuf);
    }

    @Override
    public @NotNull NetworkUnsafe networkUnsafe() {
        return packet -> this.client.write(packet);
    }

    @Override
    public void handleEntityPacket(@NotNull Packet packet) {
    }

    @Override
    protected BasicServiceConnection getConnection() {
        return this;
    }
}
