package com.github.derrop.proxy.network.handler;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.network.channel.DefaultNetworkChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public final class HandlerEndpoint extends ChannelInboundHandlerAdapter {

    private DefaultNetworkChannel networkChannel;

    private ChannelListener channelListener;

    public HandlerEndpoint(@Nullable ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    public void setChannelListener(@Nullable ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.networkChannel = new DefaultNetworkChannel(ctx);

        if (this.channelListener != null) {
            this.channelListener.handleChannelActive(this.networkChannel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (this.channelListener != null) {
            this.channelListener.handleChannelInactive(this.networkChannel);
        }

        this.networkChannel.markClosed();
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

            this.networkChannel.setAddress(newAddress);
            return;
        }

        if (msg instanceof Packet) {
            Packet result = this.getHandlers().handlePacketReceive((Packet) msg, this.networkChannel.getProtocolState(), this.networkChannel);

            if (result != null && this.channelListener != null) {
                this.channelListener.handleFinishedProceed(result);
            }
        }
    }

    @NotNull
    private PacketHandlerRegistry getHandlers() {
        return MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PacketHandlerRegistry.class);
    }

}
