package com.github.derrop.proxy.network.handler;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.network.channel.DefaultNetworkChannel;
import com.github.derrop.proxy.network.minecraft.MinecraftDecoder;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public final class HandlerEndpoint extends ChannelInboundHandlerAdapter {

    private NetworkChannel networkChannel;

    private ChannelListener channelListener;

    public HandlerEndpoint(@Nullable ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    public void setChannelListener(@Nullable ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    public void setNetworkChannel(@NotNull NetworkChannel networkChannel) {
        this.networkChannel = networkChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (this.networkChannel == null) {
            this.networkChannel = new DefaultNetworkChannel(ctx);
        }

        if (this.channelListener != null) {
            this.channelListener.handleChannelActive(this.networkChannel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (this.channelListener != null) {
            this.channelListener.handleChannelInactive(this.networkChannel);
        }

        if (this.networkChannel instanceof DefaultNetworkChannel) {
            ((DefaultNetworkChannel) this.networkChannel).markClosed();
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        if (this.channelListener != null) {
            this.channelListener.handleChannelWriteableChange(this.networkChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (this.channelListener != null) {
            this.channelListener.handleException(this.networkChannel, cause);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxy = (HAProxyMessage) msg;
            InetSocketAddress newAddress = new InetSocketAddress(proxy.sourceAddress(), proxy.sourcePort());

            if (this.networkChannel instanceof DefaultNetworkChannel) {
                ((DefaultNetworkChannel) this.networkChannel).setAddress(newAddress);
            }
            return;
        }

        ProtocolDirection direction = ctx.channel().pipeline().get(MinecraftDecoder.class).getDirection();
        if (msg instanceof DecodedPacket) {
            DecodedPacket packet = (DecodedPacket) msg;
            if (packet.getPacket() != null) {
                Packet result = this.getHandlers().handlePacketReceive(packet.getPacket(), direction, this.networkChannel.getProtocolState(), this.networkChannel);
                if (result == null) {
                    // ProceedCancelException - user stopped handling of packet (we should respect the decision)
                    return;
                }
            }

            this.getHandlers().handlePacketReceive(packet, direction, this.networkChannel.getProtocolState(), this.networkChannel);
        }
    }

    @NotNull
    private PacketHandlerRegistry getHandlers() {
        return MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PacketHandlerRegistry.class);
    }

}
