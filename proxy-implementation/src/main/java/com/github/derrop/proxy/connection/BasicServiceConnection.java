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
package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.account.BanTester;
import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.connection.ServiceWorldDataProvider;
import com.github.derrop.proxy.api.player.Player;
import com.github.derrop.proxy.api.player.PlayerAbilities;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.api.session.ProvidedSessionService;
import com.github.derrop.proxy.api.task.DefaultTask;
import com.github.derrop.proxy.api.task.EmptyTaskFutureListener;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.api.task.util.TaskUtil;
import com.github.derrop.proxy.api.util.BlockIterator;
import com.github.derrop.proxy.api.session.MCServiceCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.player.id.PlayerId;
import com.github.derrop.proxy.connection.player.DefaultPlayerAbilities;
import com.github.derrop.proxy.network.channel.WrappedNetworkChannel;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientChatMessage;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerChatMessage;
import com.github.derrop.proxy.protocol.rewrite.EntityRewrite;
import com.github.derrop.proxy.protocol.rewrite.EntityRewrite_1_8;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.netty.buffer.ByteBuf;
import net.kyori.text.Component;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BasicServiceConnection implements ServiceConnection, WrappedNetworkChannel, Entity.Callable {

    private static final Set<NetworkAddress> BANNED_ADDRESSES = new HashSet<>();

    public BasicServiceConnection(MCProxy proxy, MCServiceCredentials credentials, NetworkAddress networkAddress) throws AuthenticationException {
        this(proxy, credentials, networkAddress, true);
    }

    public BasicServiceConnection(MCProxy proxy, MCServiceCredentials credentials, NetworkAddress networkAddress, boolean reScheduleOnFailure) throws AuthenticationException {
        this.proxy = proxy;
        this.credentials = credentials;
        this.networkAddress = networkAddress;
        this.reScheduleOnFailure = reScheduleOnFailure;

        if (credentials.isOffline()) {
            this.authentication = null;
            return;
        }

        System.out.println("Logging in " + credentials.getEmail() + "...");
        this.authentication = proxy.getServiceRegistry().getProviderUnchecked(ProvidedSessionService.class).login(credentials.getEmail(), credentials.getPassword());
        System.out.println("Successfully logged in with " + credentials.getEmail() + "!");
    }

    private final MCProxy proxy;

    private final MCServiceCredentials credentials;
    private final UserAuthentication authentication;

    private final NetworkAddress networkAddress;

    private final ServiceWorldDataProvider worldDataProvider = new BasicServiceWorldDataProvider(this);

    private final EntityRewrite entityRewrite = new EntityRewrite_1_8();

    private ConnectedProxyClient client;
    private final PlayerAbilities abilities = new DefaultPlayerAbilities(this);

    private boolean reScheduleOnFailure;
    private boolean sneaking;
    private boolean sprinting;
    private Location location = new Location(0, 0, 0, 0, 0);

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
    public @NotNull Proxy getProxy() {
        return this.proxy;
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.client == null ? null : this.client.getRedirector();
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
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
    public Location getTargetBlock(int range) {
        return this.getTargetBlock(null, range);
    }

    @Override
    public Location getTargetBlock(Set<Material> transparent, int range) {
        BlockIterator iterator = new BlockIterator(this.getBlockAccess(), this.location, 1.8, range);
        while (iterator.hasNext()) {
            Location location = iterator.next();
            Material material = this.getBlockAccess().getMaterial(location);
            if ((transparent == null && material != Material.AIR) || (transparent != null && !transparent.contains(material))) {
                return location;
            }
        }
        return null;
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
        this.chat(GsonComponentSerializer.INSTANCE.serialize(component));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull Component component) {
        this.displayMessage(type, GsonComponentSerializer.INSTANCE.serialize(component));
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
    public @NotNull Task<Boolean> connect() {
        return this.connect(EmptyTaskFutureListener.BOOL_INSTANCE);
    }

    @Override
    public @NotNull Task<Boolean> connect(@NotNull TaskFutureListener<Boolean> listener) {
        return this.connect(Collections.singletonList(listener));
    }

    @Override
    public @NotNull Task<Boolean> connect(@NotNull Collection<TaskFutureListener<Boolean>> listener) {
        if (BANNED_ADDRESSES.contains(this.networkAddress)) {
            System.err.println("To prevent ip bans the connection to " + this.networkAddress + " was aborted");
            return TaskUtil.completedTask(false, listener);
        }

        Task<Boolean> task = new DefaultTask<>();
        for (TaskFutureListener<Boolean> booleanTaskFutureListener : listener) {
            task.addListener(booleanTaskFutureListener);
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            this.client = new ConnectedProxyClient(this.proxy, this);

            try {
                this.client.setAuthentication(this.authentication, this.credentials);

                Boolean result = this.client.connect(this.networkAddress, null).get(5, TimeUnit.SECONDS);
                if (result != null && result) {
                    ServiceConnector connector = this.proxy.getServiceRegistry().getProviderUnchecked(ServiceConnector.class);
                    if (connector instanceof DefaultServiceConnector) {
                        ((DefaultServiceConnector) connector).addOnlineClient(this);
                    }
                    task.complete(true);
                } else {
                    task.complete(false);
                    if (this.isReScheduleOnFailure()) {
                        this.reSchedule(listener);
                    }
                }
            } catch (final InterruptedException | ExecutionException | TimeoutException exception) {
                task.completeExceptionally(exception);
                this.client = null;

                if (exception.getCause() instanceof KickedException) {
                    if (BanTester.isBanned(exception.getMessage()) == BanTester.BanTestResult.BANNED) {
                        BANNED_ADDRESSES.add(this.networkAddress);
                        System.out.println("Preventing connections to " + networkAddress + " because " + credentials.getEmail() + " is banned");
                        return;
                    }
                }

                if (this.isReScheduleOnFailure()) {
                    this.reSchedule(listener);
                }
            }
        });
        return task;
    }

    @Override
    public @NotNull Task<Boolean> reconnect() {
        return this.reconnect(EmptyTaskFutureListener.BOOL_INSTANCE);
    }

    @Override
    public @NotNull Task<Boolean> reconnect(@NotNull TaskFutureListener<Boolean> listener) {
        return this.reconnect(Collections.singletonList(listener));
    }

    @Override
    public @NotNull Task<Boolean> reconnect(@NotNull Collection<TaskFutureListener<Boolean>> listener) {
        if (this.client == null) {
            return TaskUtil.completedTask(false, listener);
        }

        this.close();
        return this.connect(listener);
    }

    @Override
    public void setReScheduleOnFailure(boolean reScheduleOnFailure) {
        this.reScheduleOnFailure = reScheduleOnFailure;
    }

    @Override
    public boolean isReScheduleOnFailure() {
        return this.reScheduleOnFailure;
    }

    @Override
    public @NotNull SocketAddress getSocketAddress() {
        return this.client.getWrappedChannel().remoteAddress();
    }

    @Override
    public void disconnect(@NotNull String reason) {
        this.close();
    }

    @Override
    public void disconnect(@NotNull Component reason) {
        this.close();
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
    public void handleDisconnected(@NotNull ServiceConnection connection, @NotNull Component reason) {
        this.reconnect();
    }

    @Override
    public void unregister() {
        ServiceConnector connector = this.proxy.getServiceRegistry().getProviderUnchecked(ServiceConnector.class);
        if (connector instanceof DefaultServiceConnector) {
            ((DefaultServiceConnector) connector).addOnlineClient(this);
        }
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

    private void reSchedule(Collection<TaskFutureListener<Boolean>> listener) {
        if (this.client != null && this.client.isConnected()) {
            this.client.close();
            this.client = null;
        }

        try {
            Thread.sleep(20000);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }

        this.connect(listener);
    }

    @Override
    public void close() {
        if (!this.isConnected()) {
            return;
        }

        this.client.disconnect();
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        if (this.client == null || this.client.getRedirector() == null) {
            return;
        }

        this.client.write(packet);
    }

    @Override
    public void sendPacket(@NotNull ByteBuf byteBuf) {
        if (this.client == null || this.client.getRedirector() == null) {
            return;
        }

        this.client.write(byteBuf);
    }

    @Override
    public @NotNull NetworkUnsafe networkUnsafe() {
        return packet -> client.write(packet);
    }

    @Override
    public void handleEntityPacket(@NotNull Packet packet) {

    }
}
