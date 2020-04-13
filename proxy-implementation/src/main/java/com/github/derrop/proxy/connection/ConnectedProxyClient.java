package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.connection.cache.handler.ScoreboardCache;
import com.github.derrop.proxy.scoreboard.BasicScoreboard;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.task.DefaultTask;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.packet.ResourcePackSend;
import com.github.derrop.proxy.connection.cache.packet.ResourcePackStatusResponse;
import com.github.derrop.proxy.connection.cache.packet.entity.EntityMetadata;
import com.github.derrop.proxy.connection.cache.packet.entity.spawn.PositionedPacket;
import com.github.derrop.proxy.connection.cache.packet.entity.spawn.SpawnPosition;
import com.github.derrop.proxy.connection.velocity.*;
import com.github.derrop.proxy.exception.KickedException;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.minecraft.SessionUtils;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.util.NettyUtils;
import com.github.derrop.proxy.api.util.NetworkAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.proxy.Socks5ProxyHandler;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.*;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.LoginRequest;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConnectedProxyClient {

    private final MCProxy proxy;
    private final BasicServiceConnection connection;

    private NetworkAddress address;
    private MinecraftSessionService sessionService = SessionUtils.SERVICE.createMinecraftSessionService();
    private UserAuthentication authentication;
    private MCCredentials credentials;

    private ChannelWrapper channelWrapper;
    private UserConnection redirector;
    private Channel channel;

    private PacketCache packetCache = new PacketCache(this);
    private EntityMap entityMap = EntityMap.getEntityMap(47);
    private Scoreboard scoreboard;

    private int entityId;
    private int dimension;

    public int posX, posY, posZ;
    private boolean onGround;

    private EntityMetadata entityMetadata;

    private Consumer<PacketWrapper> clientPacketHandler;

    private Runnable disconnectionHandler;

    private boolean globalAccount = true;

    private Map<Predicate<DefinedPacket>, Long> blockedPackets = new ConcurrentHashMap<>();

    private BaseComponent[] lastKickReason;

    private PlayerVelocityHandler velocityHandler = new PlayerVelocityHandler(this);

    private long lastAlivePacket = -1;

    private CompletableFuture<Boolean> connectionHandler;

    public ConnectedProxyClient(MCProxy proxy, BasicServiceConnection connection) {
        this.proxy = proxy;
        this.connection = connection;

        this.scoreboard = new BasicScoreboard(connection, (ScoreboardCache) this.packetCache.getHandler(handler -> handler instanceof ScoreboardCache));
    }

    public boolean performMojangLogin(MCCredentials credentials) throws AuthenticationException {
        if (credentials.isOffline()) {
            this.credentials = credentials;
            return true;
        }
        System.out.println("Logging in " + credentials.getEmail() + "...");
        this.authentication = SessionUtils.logIn(credentials.getEmail(), credentials.getPassword());
        this.credentials = credentials;
        System.out.println("Successfully logged in with " + credentials.getEmail() + "!");
        return true;
    }

    public void setAuthentication(UserAuthentication authentication, MCCredentials credentials) {
        this.authentication = authentication;
        this.credentials = credentials;
    }

    public void disconnect() {
        if (this.channel == null || this.channelWrapper == null) {
            return;
        }

        this.channelWrapper.close();
        this.channel = null;
        this.channelWrapper = null;
        this.address = null;
        this.packetCache.reset();

        this.lastKickReason = null;
        this.lastAlivePacket = -1;
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(false);
            this.connectionHandler = null;
        }
        this.velocityHandler = new PlayerVelocityHandler(this);
        this.entityId = -1;
        this.dimension = -1;

        if (MCProxy.getInstance() != null && this.globalAccount) {
            MCProxy.getInstance().getOnlineClients().remove(this);
        }

        if (this.disconnectionHandler != null) {
            this.disconnectionHandler.run();
        }
    }

    public Task<Boolean> connect(NetworkAddress address, NetworkAddress proxy) {
        this.disconnect();

        Task<Boolean> future = new DefaultTask<>();

        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                PipelineUtils.BASE.initChannel(ch);

                if (proxy != null) {
                    ch.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(proxy.getHost(), proxy.getPort())));
                }

                ch.pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, new MinecraftEncoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().get(HandlerBoss.class).setHandler(new ProxyClientLoginHandler(ConnectedProxyClient.this, ConnectedProxyClient.this.connection));
            }
        };
        ChannelFutureListener listener = future1 -> {
            if (future1.isSuccess()) {
                this.channelWrapper = new ChannelWrapper(future1.channel());
                this.address = address;

                this.channelWrapper.write(new Handshake(47, address.getHost(), address.getPort(), 2));
                this.channelWrapper.setProtocol(Protocol.LOGIN);
                this.channelWrapper.write(new LoginRequest(this.getAccountName()));
            } else {
                future1.channel().close();
                future.complete(false);
            }
        };

        this.connectionHandler = future;

        this.channel = new Bootstrap()
                .channel(NettyUtils.getSocketChannelClass())
                .group(NettyUtils.newEventLoopGroup())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .connect(new InetSocketAddress(address.getHost(), address.getPort()))
                .addListener(listener)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .channel();

        return future;
    }

    public void setDisconnectionHandler(Runnable disconnectionHandler) {
        this.disconnectionHandler = disconnectionHandler;
    }

    public void setClientPacketHandler(Consumer<PacketWrapper> clientPacketHandler) {
        this.clientPacketHandler = clientPacketHandler;
    }

    public Consumer<PacketWrapper> getClientPacketHandler() {
        return clientPacketHandler;
    }

    public void disableGlobal() {
        this.globalAccount = false;
    }

    public MCProxy getProxy() {
        return proxy;
    }

    public ServiceConnection getConnection() {
        return connection;
    }

    public NetworkAddress getAddress() {
        return address;
    }

    public UserAuthentication getAuthentication() {
        return authentication;
    }

    public String getAccountName() {
        return this.credentials.isOffline() ? this.credentials.getUsername() : this.authentication.getSelectedProfile().getName();
    }

    public UUID getAccountUUID() {
        return this.credentials.isOffline() ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.credentials.getUsername()).getBytes()) : this.authentication.getSelectedProfile().getId();
    }

    public BaseComponent[] getLastKickReason() {
        return lastKickReason;
    }

    public void setLastKickReason(BaseComponent[] lastKickReason) {
        this.lastKickReason = lastKickReason;
    }

    public PlayerVelocityHandler getVelocityHandler() {
        return velocityHandler;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public PacketCache getPacketCache() {
        return packetCache;
    }

    public MinecraftSessionService getSessionService() {
        return sessionService;
    }

    public MCCredentials getCredentials() {
        return credentials;
    }

    public ChannelWrapper getChannelWrapper() {
        return channelWrapper;
    }

    public boolean isConnected() {
        return this.channelWrapper != null && !this.channelWrapper.isClosing() && !this.channelWrapper.isClosed();
    }

    public UserConnection getRedirector() {
        return redirector;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public EntityMap getEntityMap() {
        return entityMap;
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
        if (this.redirector != null) {
            this.packetCache.handleFree(this.redirector);
        }
        this.redirector = null;
    }

    public void keepAliveTick() {
        if (this.lastAlivePacket == -1) {
            return;
        }

        // todo implement this?
    }

    public void blockPacketUntil(Predicate<DefinedPacket> tester, long until) {
        this.blockedPackets.put(tester, until);
    }

    public void redirectPacket(ByteBuf packet, DefinedPacket deserialized) {
        if (this.channelWrapper == null || this.channelWrapper.getProtocol() != Protocol.GAME) {
            return;
        }
        if (packet == null) {
            return;
        }

        if (deserialized instanceof ResourcePackSend) {
            this.channelWrapper.write(new ResourcePackStatusResponse(((ResourcePackSend) deserialized).getHash(), ResourcePackStatusResponse.Action.ACCEPTED));
            this.channelWrapper.write(new ResourcePackStatusResponse(((ResourcePackSend) deserialized).getHash(), ResourcePackStatusResponse.Action.SUCCESSFULLY_LOADED));
            throw CancelSendSignal.INSTANCE;
        }

        if (deserialized != null && !this.blockedPackets.isEmpty()) {
            for (Map.Entry<Predicate<DefinedPacket>, Long> entry : this.blockedPackets.entrySet()) {
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
            this.velocityHandler.handlePacket(ProtocolConstants.Direction.TO_CLIENT, deserialized);
        }

        if (this.redirector != null) {
            if (deserialized != null) { // rewrite to allow modifications by the packet handlers
                int id = DefinedPacket.readVarInt(packet);

                ByteBuf buf = Unpooled.buffer();
                DefinedPacket.writeVarInt(id, buf);

                deserialized.write(buf, ProtocolConstants.Direction.TO_CLIENT, 47);

                this.redirector.getCh().write(buf);
            } else {
                this.redirector.getCh().write(packet);
            }

            if (!this.redirector.isConnected()) {
                this.redirector = null;
            }
        }

        if (deserialized instanceof PositionedPacket) {
            if (((PositionedPacket) deserialized).getEntityId() == this.getEntityId()) {
                this.posX = ((PositionedPacket) deserialized).getX();
                this.posY = ((PositionedPacket) deserialized).getY();
                this.posZ = ((PositionedPacket) deserialized).getZ();
            }
        } else if (deserialized instanceof SpawnPosition) {
            BlockPos pos = ((SpawnPosition) deserialized).getSpawnPosition();
            this.posX = pos.getX();
            this.posY = pos.getY();
            this.posZ = pos.getZ();
        } else if (deserialized instanceof EntityMetadata) {
            this.entityMetadata = (EntityMetadata) deserialized;
        }
    }

    public void handleClientPacket(PacketWrapper packetWrapper) {
        if (this.clientPacketHandler != null) {
            this.clientPacketHandler.accept(packetWrapper);
        }

        DefinedPacket deserialized = packetWrapper.packet;

        if (deserialized instanceof PositionedPacket) {
            this.posX = ((PositionedPacket) deserialized).getX();
            this.posY = ((PositionedPacket) deserialized).getY();
            this.posZ = ((PositionedPacket) deserialized).getZ();
        }

        if (packetWrapper.packet instanceof PlayerPosLook) {
            this.onGround = ((PlayerPosLook) packetWrapper.packet).isOnGround();
        } else if (packetWrapper.packet instanceof PlayerLook) {
            this.onGround = ((PlayerLook) packetWrapper.packet).isOnGround();
        } else if (packetWrapper.packet instanceof PlayerPosition) {
            this.onGround = ((PlayerPosition) packetWrapper.packet).isOnGround();
        } else if (packetWrapper.packet instanceof Player) {
            this.onGround = ((Player) packetWrapper.packet).isOnGround();
        }
    }

    public void redirectPackets(UserConnection con, boolean switched) {
        this.packetCache.send(con, switched);
        this.redirector = con;
    }

    public void connectionSuccess() {
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(true);
            this.connectionHandler = null;
        }
    }

    public void connectionFailed() {
        if (this.connectionHandler != null) {
            this.connectionHandler.completeExceptionally(new KickedException(TextComponent.toLegacyText(this.lastKickReason)));
            this.connectionHandler = null;
        }
    }

}
