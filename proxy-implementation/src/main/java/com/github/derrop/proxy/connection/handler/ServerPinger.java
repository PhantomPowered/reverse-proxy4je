package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.network.channel.DefaultNetworkChannel;
import com.github.derrop.proxy.network.handler.HandlerEndpoint;
import com.github.derrop.proxy.network.minecraft.MinecraftDecoder;
import com.github.derrop.proxy.network.minecraft.MinecraftEncoder;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingClientSetProtocol;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInRequest;
import com.github.derrop.proxy.api.task.DefaultTask;
import com.github.derrop.proxy.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class ServerPinger extends DefaultNetworkChannel implements ChannelListener {

    private Task<ServerPing> task;

    private int protocol = 47;
    private ProxyHandler proxyHandler;

    public Task<ServerPing> ping(MCProxy proxy, NetworkAddress address) {
        if (this.task != null) {
            throw new IllegalStateException("This pinger is already used");
        }

        this.task = new DefaultTask<>();

        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                proxy.getBaseChannelInitializer().initChannel(ch);

                if (proxyHandler != null) {
                    ch.pipeline().addFirst(proxyHandler);
                }

                ch.pipeline().addAfter(NetworkUtils.LENGTH_DECODER, NetworkUtils.PACKET_DECODER, new MinecraftDecoder(proxy.getServiceRegistry(), ProtocolDirection.TO_CLIENT, ProtocolState.HANDSHAKING));
                ch.pipeline().addAfter(NetworkUtils.LENGTH_ENCODER, NetworkUtils.PACKET_ENCODER, new MinecraftEncoder(ProtocolDirection.TO_SERVER));
                ch.pipeline().get(HandlerEndpoint.class).setNetworkChannel(ServerPinger.this);
                ch.pipeline().get(HandlerEndpoint.class).setChannelListener(ServerPinger.this);
            }
        };
        ChannelFutureListener listener = future1 -> {
            if (future1.isSuccess()) {
                super.setChannel(future1.channel());

                super.write(new PacketHandshakingClientSetProtocol(protocol, address.getHost(), address.getPort(), 1));
                super.setProtocolState(ProtocolState.STATUS);
                super.write(new PacketStatusInRequest());
            } else {
                future1.channel().close();
                task.complete(null);
            }
        };

        new Bootstrap()
                .channel(NettyUtils.getSocketChannelClass())
                .group(NettyUtils.newEventLoopGroup())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .connect(new InetSocketAddress(address.getHost(), address.getPort()))
                .addListener(listener)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

        return this.task;
    }

    public void complete(ServerPing serverPing) {
        if (this.task != null) {
            this.task.complete(serverPing);
            super.close();
        }
    }

    public ServerPinger protocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    public ServerPinger socks5Proxy(NetworkAddress address) {
        this.proxyHandler = new Socks5ProxyHandler(new InetSocketAddress(address.getHost(), address.getPort()));
        return this;
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (this.task != null && !this.task.isDone()) {
            this.task.completeExceptionally(cause);
        }
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        if (this.task != null && !this.task.isDone()) {
            this.task.complete(null);
        }
    }
}
