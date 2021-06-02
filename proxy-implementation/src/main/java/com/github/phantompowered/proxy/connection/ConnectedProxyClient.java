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

import com.github.phantompowered.proxy.ImplementationUtil;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.connection.ServiceConnectResult;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.service.ServiceConnectEvent;
import com.github.phantompowered.proxy.api.events.connection.service.ServiceDisconnectEvent;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.scoreboard.Scoreboard;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.session.ProvidedSessionService;
import com.github.phantompowered.proxy.api.task.Task;
import com.github.phantompowered.proxy.api.tick.TickHandler;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.handler.scoreboard.ScoreboardCache;
import com.github.phantompowered.proxy.connection.login.ProxyClientLoginListener;
import com.github.phantompowered.proxy.connection.player.DefaultPlayer;
import com.github.phantompowered.proxy.connection.player.scoreboard.BasicScoreboard;
import com.github.phantompowered.proxy.connection.velocity.PlayerVelocityHandler;
import com.github.phantompowered.proxy.network.NetworkUtils;
import com.github.phantompowered.proxy.network.SimpleChannelInitializer;
import com.github.phantompowered.proxy.network.channel.DefaultNetworkChannel;
import com.github.phantompowered.proxy.network.pipeline.handler.HandlerEndpoint;
import com.github.phantompowered.proxy.network.pipeline.minecraft.MinecraftDecoder;
import com.github.phantompowered.proxy.network.pipeline.minecraft.MinecraftEncoder;
import com.github.phantompowered.proxy.protocol.handshake.PacketHandshakingClientSetProtocol;
import com.github.phantompowered.proxy.protocol.login.client.PacketLoginClientLoginRequest;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientResourcePackStatusResponse;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerResourcePackSend;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class ConnectedProxyClient extends DefaultNetworkChannel implements TickHandler {

    private final BasicServiceConnection connection;
    private final MinecraftSessionService sessionService;
    private final Collection<Player> viewers = new CopyOnWriteArrayList<>();
    private final UUID redirectorListenerKey = UUID.randomUUID();
    private final PacketCache packetCache;
    private final Map<Predicate<Packet>, Long> blockedPackets = new ConcurrentHashMap<>();
    private Scoreboard scoreboard;
    private NetworkAddress address;
    private UserAuthentication authentication;
    private MCServiceCredentials credentials;
    private Player redirector;
    private int entityId;
    private int dimension;
    private Runnable disconnectionHandler;
    private Component lastKickReason;
    private PlayerVelocityHandler velocityHandler = new PlayerVelocityHandler(this);

    private long lastAlivePacket = -1;
    private long lastDisconnectionTimestamp = System.currentTimeMillis();

    private CompletableFuture<ServiceConnectResult> connectionHandler;
    private PlayerId lastConnectedPlayer;
    private boolean receivedServerDisconnect = false;

    public ConnectedProxyClient(ServiceRegistry serviceRegistry, BasicServiceConnection connection) {
        super(serviceRegistry);
        this.connection = connection;

        this.sessionService = serviceRegistry.getProviderUnchecked(ProvidedSessionService.class).createSessionService();

        this.packetCache = new PacketCache(this);
        this.resetPacketCache();
    }

    private void resetPacketCache() {
        this.packetCache.reset();
        this.scoreboard = new BasicScoreboard(connection, this.packetCache.getHandler(ScoreboardCache.class));
    }

    public void setAuthentication(UserAuthentication authentication, MCServiceCredentials credentials) {
        this.authentication = authentication;
        this.credentials = credentials;
    }

    public void disconnect() {
        if (super.getWrappedChannel() == null) {
            return;
        }

        super.close();
        this.address = null;
        this.resetPacketCache();

        if (this.connectionHandler != null) {
            this.connectionHandler.complete(ServiceConnectResult.failure(this.lastKickReason));
            this.connectionHandler = null;
        }

        this.lastKickReason = null;
        this.lastAlivePacket = -1;
        this.velocityHandler = new PlayerVelocityHandler(this);
        this.entityId = -1;
        this.dimension = -1;

        if (this.disconnectionHandler != null) {
            this.disconnectionHandler.run();
        }
    }

    public void connect(NetworkAddress address, NetworkAddress proxy, Task<ServiceConnectResult> task) {
        this.disconnect();

        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(@NotNull Channel channel) {
                ConnectedProxyClient.this.serviceRegistry.getProviderUnchecked(SimpleChannelInitializer.class).initChannel(channel);

                if (proxy != null) {
                    channel.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(proxy.getHost(), proxy.getPort())));
                }

                channel.pipeline().addAfter(NetworkUtils.LENGTH_DECODER, NetworkUtils.PACKET_DECODER,
                        new MinecraftDecoder(ConnectedProxyClient.this.serviceRegistry, ProtocolDirection.TO_CLIENT, ProtocolState.HANDSHAKING));
                channel.pipeline().addAfter(NetworkUtils.LENGTH_ENCODER, NetworkUtils.PACKET_ENCODER, new MinecraftEncoder(connection.getServiceRegistry(), ProtocolDirection.TO_SERVER));
                channel.pipeline().get(HandlerEndpoint.class).setNetworkChannel(ConnectedProxyClient.this);
                channel.pipeline().get(HandlerEndpoint.class).setChannelListener(new ProxyClientLoginListener(ConnectedProxyClient.this));
            }
        };
        ChannelFutureListener listener = future -> {
            if (future.isSuccess()) {
                super.setChannel(future.channel());
                this.address = address;

                super.write(new PacketHandshakingClientSetProtocol(47, address.getHost(), address.getPort(), 2));
                super.setProtocolState(ProtocolState.LOGIN);
                super.write(new PacketLoginClientLoginRequest(this.getAccountName()));
            } else {
                future.channel().close();
                task.complete(ServiceConnectResult.failure(future.cause() == null ? null : Component.text(ImplementationUtil.stringifyException(future.cause()))));
            }
        };

        this.connectionHandler = task;

        new Bootstrap()
                .channel(NetworkUtils.getSocketChannelClass())
                .group(NetworkUtils.newEventLoopGroup())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .connect(new InetSocketAddress(address.getHost(), address.getPort()))
                .addListener(listener)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void setDisconnectionHandler(Runnable disconnectionHandler) {
        this.disconnectionHandler = disconnectionHandler;
    }

    public long getLastDisconnectionTimestamp() {
        return this.lastDisconnectionTimestamp;
    }

    public PlayerId getLastConnectedPlayer() {
        return this.lastConnectedPlayer;
    }

    public BasicServiceConnection getConnection() {
        return this.connection;
    }

    public CompletableFuture<ServiceConnectResult> getConnectionHandler() {
        return this.connectionHandler;
    }

    public void setConnectionHandler(CompletableFuture<ServiceConnectResult> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public NetworkAddress getServerAddress() {
        return this.address;
    }

    public UserAuthentication getAuthentication() {
        return this.authentication;
    }

    public String getAccountName() {
        return this.credentials.isOffline() ? this.credentials.getUsername() : this.authentication.getSelectedProfile().getName();
    }

    public UUID getAccountUUID() {
        return this.credentials.isOffline() ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.credentials.getUsername()).getBytes()) : this.authentication.getSelectedProfile().getId();
    }

    public Component getLastKickReason() {
        return this.lastKickReason;
    }

    public void setLastKickReason(Component lastKickReason) {
        this.lastKickReason = lastKickReason;
    }

    public PlayerVelocityHandler getVelocityHandler() {
        return this.velocityHandler;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public PacketCache getPacketCache() {
        return this.packetCache;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public MCServiceCredentials getCredentials() {
        return credentials;
    }

    public Player getRedirector() {
        return redirector;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public long getLastAlivePacket() {
        return lastAlivePacket;
    }

    public void setLastAlivePacket(long lastAlivePacket) {
        this.lastAlivePacket = lastAlivePacket;
    }

    public void free() {
        Player redirector = this.redirector;

        if (redirector != null) {
            this.packetCache.handleFree(redirector);
            redirector.removeOutgoingPacketListener(this.redirectorListenerKey);

            this.lastDisconnectionTimestamp = System.currentTimeMillis();
            this.lastConnectedPlayer = new PlayerId(redirector.getUniqueId(), redirector.getName());
        }

        this.redirector = null;
    }

    public void blockPacketUntil(Predicate<Packet> tester, long until) {
        this.blockedPackets.put(tester, until);
    }

    public void redirectPacket(ProtoBuf packet, Packet deserialized) {
        if (!this.isConnected() || super.getProtocolState() != ProtocolState.PLAY) {
            return;
        }
        if (packet == null) {
            return;
        }

        if (deserialized instanceof PacketPlayServerResourcePackSend) {
            super.write(new PacketPlayClientResourcePackStatusResponse(((PacketPlayServerResourcePackSend) deserialized).getHash(), PacketPlayClientResourcePackStatusResponse.Action.ACCEPTED));
            super.write(new PacketPlayClientResourcePackStatusResponse(((PacketPlayServerResourcePackSend) deserialized).getHash(), PacketPlayClientResourcePackStatusResponse.Action.SUCCESSFULLY_LOADED));
            throw CancelProceedException.INSTANCE;
        }

        if (deserialized != null && !this.blockedPackets.isEmpty()) {
            for (Map.Entry<Predicate<Packet>, Long> entry : this.blockedPackets.entrySet()) {
                if (entry.getValue() >= System.currentTimeMillis()) {
                    this.blockedPackets.remove(entry.getKey());
                    continue;
                }

                if (entry.getKey().test(deserialized)) {
                    return;
                }
            }
        }

        this.packetCache.handlePacket(packet, deserialized);
        if (deserialized != null) {
            this.velocityHandler.handlePacket(ProtocolDirection.TO_CLIENT, deserialized);
        }

        if (this.redirector != null) {
            if (deserialized != null) {
                // rewrite to allow modifications by the packet handlers
                this.redirector.sendPacket(deserialized);
                for (Player viewer : this.viewers) {
                    viewer.sendPacket(deserialized);
                }
            } else {
                this.redirector.sendPacket(packet);
                for (Player viewer : this.viewers) {
                    viewer.sendPacket(packet);
                }
            }


            if (!this.redirector.isConnected()) {
                this.redirector = null;
            }
        }
    }

    public void handlePacketRedirected(Packet packet) {
        if (this.redirector != null) {
            this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).handlePacketReceive(packet, ProtocolDirection.TO_CLIENT, ProtocolState.REDIRECTING, this.connection);
        }
    }

    public void handleClientPacket(Packet packet) {
        this.packetCache.handleClientPacket(packet);

        if (!this.viewers.isEmpty()) {
            Packet serverPacket = packet.mapToServerside(this.entityId);

            if (serverPacket != null) {
                for (Player viewer : this.viewers) {
                    viewer.sendPacket(serverPacket);
                }
            }
        }
    }

    public void addViewer(Player player) {
        this.packetCache.send(player, false);
        player.sendPacket(new PacketPlayServerEntityTeleport(this.entityId, this.connection.getLocation()));
        this.viewers.add(player);
    }

    public Collection<Player> getViewers() {
        return this.viewers;
    }

    @Override
    public void handleTick() {
        if (this.viewers.isEmpty()) {
            return;
        }
        Packet packet = new PacketPlayServerEntityTeleport(this.entityId, this.connection.getLocation());
        for (Player viewer : this.viewers) {
            viewer.sendPacket(packet);
        }
    }

    public void redirectPackets(Player con, boolean switched) {
        this.redirector = con;
        con.addOutgoingPacketListener(this.redirectorListenerKey, this::handlePacketRedirected);

        this.packetCache.send(con, switched);
        con.sendPacket(new PacketPlayServerEntityTeleport(this.entityId, this.connection.getLocation()));
    }

    public void connectionSuccess() {
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(ServiceConnectResult.success());
            this.connectionHandler = null;

            this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new ServiceConnectEvent(this.connection));
        }
    }

    public void connectionFailed() {
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(ServiceConnectResult.failure(this.lastKickReason));
            this.connectionHandler = null;
        }
    }

    public void handleDisconnect(Component reason) {
        if (this.receivedServerDisconnect) {
            return;
        }
        this.receivedServerDisconnect = true;

        System.out.println("Disconnected " + this.getCredentials() + " (" + this.getAccountName() + "#" + this.getAccountUUID() + ") with "
                + LegacyComponentSerializer.legacySection().serialize(reason));

        if (this.getRedirector() != null) {
            Player con = this.getRedirector();
            this.connection.getClient().free();
            if (con instanceof DefaultPlayer) {
                ((DefaultPlayer) con).handleDisconnected(this.connection, reason);
            }
        }

        this.connection.getClient().setLastKickReason(reason);
        this.connection.getClient().connectionFailed();

        this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new ServiceDisconnectEvent(this.connection, reason));
    }
}
