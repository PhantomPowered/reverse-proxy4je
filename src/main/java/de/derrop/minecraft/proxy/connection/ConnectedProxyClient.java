package de.derrop.minecraft.proxy.connection;

import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.velocity.PlayerVelocityHandler;
import de.derrop.minecraft.proxy.exception.KickedException;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.minecraft.SessionUtils;
import de.derrop.minecraft.proxy.util.NettyUtils;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.function.Predicate;

public class ConnectedProxyClient {

    private NetworkAddress address;
    private MinecraftSessionService sessionService = SessionUtils.SERVICE.createMinecraftSessionService();
    private UserAuthentication authentication;
    private MCCredentials credentials;
    private ChannelWrapper channelWrapper;
    private UserConnection redirector;
    private Channel channel;
    private PacketCache packetCache = new PacketCache(this);
    private EntityMap entityMap = EntityMap.getEntityMap(47);
    private int entityId;
    private int dimension;

    private boolean globalAccount = true;

    private Map<Predicate<DefinedPacket>, Long> blockedPackets = new ConcurrentHashMap<>();

    private BaseComponent[] lastKickReason;

    private PlayerVelocityHandler velocityHandler = new PlayerVelocityHandler(this);

    private long lastAlivePacket = -1;

    private CompletableFuture<Boolean> connectionHandler;

    public boolean performMojangLogin(MCCredentials credentials) throws AuthenticationException {
        System.out.println("Logging in " + credentials.getEmail() + "...");
        this.authentication = SessionUtils.logIn(credentials.getEmail(), credentials.getPassword());
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
    }

    public CompletableFuture<Boolean> connect(NetworkAddress address, NetworkAddress proxy) {
        this.disconnect();

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ChannelInitializer initializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                PipelineUtils.BASE.initChannel(ch);

                if (proxy != null) {
                    ch.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(proxy.getHost(), proxy.getPort())));
                }

                ch.pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, new MinecraftEncoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().get(HandlerBoss.class).setHandler(new ProxyClientLoginHandler(ConnectedProxyClient.this));
            }
        };
        ChannelFutureListener listener = future1 -> {
            if (future1.isSuccess()) {
                this.channelWrapper = new ChannelWrapper(future1.channel());
                this.address = address;

                this.channelWrapper.write(new Handshake(47, address.getHost(), address.getPort(), 2));
                this.channelWrapper.setProtocol(Protocol.LOGIN);
                this.channelWrapper.write(new LoginRequest(authentication.getSelectedProfile().getName()));
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

    public void disableGlobal() {
        this.globalAccount = false;
    }

    public NetworkAddress getAddress() {
        return address;
    }

    public UserAuthentication getAuthentication() {
        return authentication;
    }

    public String getAccountName() {
        return this.authentication.getSelectedProfile().getName();
    }

    public UUID getAccountUUID() {
        return this.authentication.getSelectedProfile().getId();
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
        if (this.channelWrapper.getProtocol() != Protocol.GAME) {
            return;
        }
        if (packet == null) {
            return;
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
            this.redirector.getCh().write(packet);

            if (!this.redirector.isConnected()) {
                this.redirector = null;
            }
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

            if (MCProxy.getInstance() != null && this.globalAccount) {
                MCProxy.getInstance().getOnlineClients().add(this);
            }
        }
    }

    public void connectionFailed() {
        if (this.connectionHandler != null) {
            this.connectionHandler.completeExceptionally(new KickedException(TextComponent.toLegacyText(this.lastKickReason)));
            this.connectionHandler = null;
        }
    }

}
