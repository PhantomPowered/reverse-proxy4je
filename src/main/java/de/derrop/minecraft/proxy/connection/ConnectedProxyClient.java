package de.derrop.minecraft.proxy.connection;

import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.minecraft.SessionUtils;
import de.derrop.minecraft.proxy.util.NettyUtils;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.LoginRequest;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ConnectedProxyClient {

    private NetworkAddress address;
    private MinecraftSessionService sessionService = SessionUtils.SERVICE.createMinecraftSessionService();
    private UserAuthentication authentication;
    private MCCredentials credentials;
    private ChannelWrapper channelWrapper;
    private UserConnection redirector;
    //private Collection<byte[]> initPackets = new ArrayList<>(); // chunks, tablist
    private PacketCache packetCache = new PacketCache();
    private Scoreboard scoreboard = new Scoreboard();
    private EntityMap entityMap = EntityMap.getEntityMap(47);
    private int entityId;
    private int dimension;

    private CompletableFuture<Boolean> connectionHandler;

    public boolean performMojangLogin(MCCredentials credentials) {
        boolean success = (this.authentication = SessionUtils.logIn(credentials.getEmail(), credentials.getPassword())) != null;
        if (success) {
            this.credentials = credentials;
        }
        return success;
    }

    public CompletableFuture<Boolean> connect(NetworkAddress address) {
        if (this.address != null) {
            throw new IllegalArgumentException("Already connected client");
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

        new Bootstrap()
                .channel(NettyUtils.getSocketChannelClass())
                .group(NettyUtils.newEventLoopGroup())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .connect(new InetSocketAddress(address.getHost(), address.getPort()))
                .addListener(listener);

        return future;
    }

    public NetworkAddress getAddress() {
        return address;
    }

    public UserAuthentication getAuthentication() {
        return authentication;
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

    public void free() {
        this.redirector = null;
    }

    public void redirectPacket(ByteBuf packet) {
        this.packetCache.handlePacket(packet);
    }

    public void redirectPackets(UserConnection con) {
        this.packetCache.send(con.getCh());
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
        }
    }

}
