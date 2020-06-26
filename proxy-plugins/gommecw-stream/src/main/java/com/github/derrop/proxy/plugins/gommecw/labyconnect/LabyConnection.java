package com.github.derrop.proxy.plugins.gommecw.labyconnect;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.session.ProvidedSessionService;
import com.github.derrop.proxy.api.task.DefaultTask;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.CryptManager;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.EnumConnectionState;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPing;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPong;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserData;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserStatus;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.UnresolvedAddressException;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Sharable
public class LabyConnection extends PacketHandler {

    private final ServiceRegistry registry;
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Chat#%d").build());
    private final ExecutorService executorService = Executors.newFixedThreadPool(2, (new ThreadFactoryBuilder()).setNameFormat("Helper#%d").build());

    private final UserAuthentication authentication;

    private Channel channel;
    private Bootstrap bootstrap;
    private EnumConnectionState currentConnectionState = EnumConnectionState.OFFLINE;
    private String lastKickMessage = "Unknown";

    private Task<UserData[]> requestedUserData;

    public LabyConnection(ServiceRegistry registry, UserAuthentication authentication) {
        this.registry = registry;
        this.authentication = authentication;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public EnumConnectionState getCurrentConnectionState() {
        return this.currentConnectionState;
    }

    public String getLastKickMessage() {
        return this.lastKickMessage;
    }

    public void close() {
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
    }

    public void connect() {
        this.connect("mod.labymod.net", 30336);
    }

    public void connect(final String ip, final int port) {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close();
            this.channel = null;
        }

        updateConnectionState(EnumConnectionState.HELLO);

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.eventLoopGroup);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, Boolean.TRUE);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(new ClientChannelInitializer(this));
        try {
            System.out.println("Connecting to " + ip + ":" + port);
            LabyConnection.this.bootstrap.connect(ip, port).syncUninterruptibly();

            LabyConnection.this.sendPacket(new LabyPacketHelloPing(System.currentTimeMillis()));
        } catch (UnresolvedAddressException error) {


            LabyConnection.this.updateConnectionState(EnumConnectionState.OFFLINE);


            LabyConnection.this.lastKickMessage = (error.getMessage() == null) ? "Unknown error" : error.getMessage();
            System.err.println("UnresolvedAddressException: " + error.getMessage());
            error.printStackTrace();
        } catch (Throwable throwable) {


            LabyConnection.this.updateConnectionState(EnumConnectionState.OFFLINE);


            LabyConnection.this.lastKickMessage = (throwable.getMessage() == null) ? "Unknown error" : throwable.getMessage();
            System.err.println("Throwable: " + throwable.getMessage());
            throwable.printStackTrace();


            if (LabyConnection.this.lastKickMessage.contains("no further information") || throwable.getMessage() == null) {
                LabyConnection.this.lastKickMessage = ("chat_not_reachable");
            }
        }
    }


    public void disconnect(final boolean kicked) {
        if (this.currentConnectionState == EnumConnectionState.OFFLINE) {
            return;
        }

        updateConnectionState(EnumConnectionState.OFFLINE);
    }


    public void updateConnectionState(EnumConnectionState connectionState) {
        this.currentConnectionState = connectionState;
    }


    public void handle(LabyPacketHelloPong packet) {
        updateConnectionState(EnumConnectionState.LOGIN);

        sendPacket(new LabyPacketLoginData(
                this.authentication.getSelectedProfile().getId(),
                this.authentication.getSelectedProfile().getName(),
                "my status"
        ));
        sendPacket(new LabyPacketLoginOptions(false, UserStatus.AWAY, TimeZone.getDefault()));
        sendPacket(new LabyPacketLoginVersion(24, "1.8.9" + "_" + "3.6.9"));


        JsonArray addons = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.add("addons", addons);
        sendPacket(new LabyPacketAddonMessage("labymod_addons", obj.toString()));
    }

    @Override
    public void handle(LabyPacketUserBadge packet) {
        UserData[] result = new UserData[packet.getUuids().length];
        for (int i = 0; i < packet.getUuids().length; i++) {
            result[i] = new UserData(packet.getUuids()[i], packet.getRanks()[i]);
        }
        if (this.requestedUserData != null) {
            this.requestedUserData.complete(result);
            this.requestedUserData = null;
        }
    }

    public void handle(LabyPacketLoginComplete packet) {
        updateConnectionState(EnumConnectionState.PLAY);

        System.out.println("login complete");
    }


    public void handle(LabyPacketKick packet) {
        disconnect(true);

        this.lastKickMessage = (packet.getReason() == null) ? ("chat_unknown_kick_reason") : packet.getReason();
        System.out.println("Kicked with " + this.lastKickMessage);
    }


    public void handle(LabyPacketDisconnect packet) {
        disconnect(true);

        this.lastKickMessage = (packet.getReason() == null) ? ("chat_unknown_disconnect_reason") : packet.getReason();
        System.out.println("Disconnected with " + this.lastKickMessage);
    }


    public void handle(LabyPacketBanned packet) {
        disconnect(true);

        this.lastKickMessage = (packet.getReason() == null) ? ("chat_unknown_ban_reason") : packet.getReason();
        System.out.println("Banned: " + this.lastKickMessage);
    }

    public void handle(LabyPacketEncryptionRequest encryptionRequest) {
        SecretKey secretKey = CryptManager.createNewSharedKey();
        PublicKey publicKey = CryptManager.decodePublicKey(encryptionRequest.getPublicKey());
        String serverId = encryptionRequest.getServerId();

        String hash = (new BigInteger(CryptManager.getServerIdHash(serverId, publicKey, secretKey))).toString(16);

        try {
            MinecraftSessionService minecraftSessionService = this.registry.getProviderUnchecked(ProvidedSessionService.class).createSessionService();
            minecraftSessionService.joinServer(this.authentication.getSelectedProfile(), this.authentication.getAuthenticatedToken(), hash);

            sendPacket(new LabyPacketEncryptionResponse(secretKey, publicKey, encryptionRequest.getVerifyToken()));
            return;
        } catch (Throwable e1) {
            e1.printStackTrace();
        }


        System.out.println(this.lastKickMessage);
        disconnect(false);
    }

    public void handle(LabyPacketAddonMessage packet) {
        System.out.println("Addon message (" + packet.getData().length + "): " + packet.getJson());
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        disconnect(false);

        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public Task<UserData[]> requestUserData(UUID... uuids) {
        Preconditions.checkArgument(this.requestedUserData == null, "Cannot make multiple user data requests at the same time");
        this.requestedUserData = new DefaultTask<>();
        this.executorService.execute(() -> this.sendPacket(new LabyPacketUserBadge(uuids)));
        return this.requestedUserData;
    }


    public void sendPacket(final LabyPacket labyPacket) {
        if (this.channel == null) {
            return;
        }
        if (!this.channel.isOpen() || !this.channel.isWritable() || this.currentConnectionState == EnumConnectionState.OFFLINE) {
            return;
        }
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.writeAndFlush(labyPacket).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() ->
                    LabyConnection.this.channel.writeAndFlush(labyPacket).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE));
        }
    }
}


