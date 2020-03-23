package de.derrop.minecraft.proxy.connection;

import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.velocity.PlayerVelocityHandler;
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
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.*;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.LoginRequest;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConnectedProxyClient {

    private NetworkAddress address;
    private MinecraftSessionService sessionService = SessionUtils.SERVICE.createMinecraftSessionService();
    private UserAuthentication authentication;
    private MCCredentials credentials;
    private ChannelWrapper channelWrapper;
    private UserConnection redirector;
    private Channel channel;
    //private Collection<byte[]> initPackets = new ArrayList<>(); // chunks, tablist
    private PacketCache packetCache = new PacketCache();
    private Scoreboard scoreboard = new Scoreboard();
    private EntityMap entityMap = EntityMap.getEntityMap(47);
    private int entityId;
    private int dimension;

    private PlayerVelocityHandler velocityHandler = new PlayerVelocityHandler(this);

    private long lastAlivePacket = -1;

    private CompletableFuture<Boolean> connectionHandler;

    public boolean performMojangLogin(MCCredentials credentials) {
        System.out.println("Logging in " + credentials.getEmail() + "...");
        boolean success = (this.authentication = SessionUtils.logIn(credentials.getEmail(), credentials.getPassword())) != null;
        if (success) {
            System.out.println("Successfully logged in with " + credentials.getEmail() + "!");
            this.credentials = credentials;
        }
        return success;
    }

    public CompletableFuture<Boolean> connect(NetworkAddress address) {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
            this.address = null;
            this.packetCache.reset();
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ChannelInitializer initializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                PipelineUtils.BASE.initChannel(ch);
                ch.pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, new MinecraftEncoder(Protocol.HANDSHAKE, false, 47));
                ch.pipeline().get(HandlerBoss.class).setHandler(new ProxyClientLoginHandler(ConnectedProxyClient.this));
            }
        };
        ChannelFutureListener listener = future1 -> {
            if (future1.isSuccess()) {
                this.channelWrapper = new ChannelWrapper(future1.channel());
                this.address = address;

                this.connectionHandler = future;

                this.channelWrapper.write(new Handshake(47, address.getHost(), address.getPort(), 2));
                this.channelWrapper.setProtocol(Protocol.LOGIN);
                this.channelWrapper.write(new LoginRequest(authentication.getSelectedProfile().getName()));
            } else {
                future1.channel().close();
                future.complete(false);
            }
        };

        this.channel = new Bootstrap()
                .channel(NettyUtils.getSocketChannelClass())
                .group(NettyUtils.newEventLoopGroup())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .connect(new InetSocketAddress(address.getHost(), address.getPort()))
                .addListener(listener)
                .channel();

        return future;
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

    public Scoreboard getScoreboard() {
        return scoreboard;
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

    public void redirectPacket(ByteBuf packet, DefinedPacket deserialized) {
        if (this.channelWrapper.getProtocol() != Protocol.GAME) {
            return;
        }
        if (packet == null) {
            return;
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

    /*
    public void redirectPacket(ByteBuf packet) {
        if (this.channelWrapper.getProtocol() != Protocol.GAME) {
            return;
        }
        if (packet == null) {
            return;
        }
        packet.markReaderIndex();
        byte[] bytes = new byte[packet.readableBytes()];
        packet.readBytes(bytes);
        this.initPackets.add(bytes);
        packet.resetReaderIndex();
        if (this.redirector != null) {
            this.redirector.getCh().write(packet);

            if (!this.redirector.isConnected()) {
                this.redirector = null;
            }
        }
    }

    public void redirectPackets(UserConnection con) {
        for (byte[] initPacket : new ArrayList<>(this.initPackets)) {
            con.getCh().write(Unpooled.wrappedBuffer(initPacket));
        }
        this.redirector = con;
    }
    */

    public void connectionSuccess() {
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(true);
            this.connectionHandler = null;

            MCProxy.getInstance().getOnlineClients().add(this);
        }
    }

    public void connectionFailed() {
        if (this.connectionHandler != null) {
            this.connectionHandler.complete(false);
            this.connectionHandler = null;
        }
    }

}
