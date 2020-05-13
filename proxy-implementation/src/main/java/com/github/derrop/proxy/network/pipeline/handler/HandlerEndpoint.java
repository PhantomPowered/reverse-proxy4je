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
package com.github.derrop.proxy.network.pipeline.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.network.channel.DefaultNetworkChannel;
import com.github.derrop.proxy.network.channel.WrappedNetworkChannel;
import com.github.derrop.proxy.network.pipeline.minecraft.MinecraftDecoder;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public final class HandlerEndpoint extends ChannelInboundHandlerAdapter {

    private final ServiceRegistry registry;

    private NetworkChannel networkChannel;

    private ChannelListener channelListener;

    public HandlerEndpoint(@NotNull ServiceRegistry registry, @Nullable ChannelListener channelListener) {
        this.registry = registry;
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

        NetworkChannel networkChannel = this.networkChannel;
        while (networkChannel instanceof WrappedNetworkChannel) {
            networkChannel = ((WrappedNetworkChannel) networkChannel).getWrappedNetworkChannel();
        }

        if (networkChannel instanceof DefaultNetworkChannel) {
            ((DefaultNetworkChannel) networkChannel).markClosed();
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
        return this.registry.getProviderUnchecked(PacketHandlerRegistry.class);
    }

}