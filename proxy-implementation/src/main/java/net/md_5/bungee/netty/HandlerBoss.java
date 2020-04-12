package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.protocol.PacketWrapper;

import java.net.InetSocketAddress;

/**
 * This class is a primitive wrapper for {@link PacketHandler} instances tied to
 * channels to maintain simple states, and only call the required, adapted
 * methods when the channel is connected.
 */
public class HandlerBoss extends ChannelInboundHandlerAdapter {

    private ChannelWrapper channel;
    private PacketHandler handler;

    public void setHandler(PacketHandler handler) {
        Preconditions.checkArgument(handler != null, "handler");
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            channel = new ChannelWrapper(ctx);
            handler.connected(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            channel.markClosed();
            handler.disconnected(channel);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            handler.writabilityChanged(channel);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxy = (HAProxyMessage) msg;
            InetSocketAddress newAddress = new InetSocketAddress(proxy.sourceAddress(), proxy.sourcePort());

            channel.setRemoteAddress(newAddress);
            return;
        }

        if (handler != null) {
            PacketWrapper packet = (PacketWrapper) msg;
            boolean sendPacket = handler.shouldHandle(packet);
            try {
                if (sendPacket && packet.packet != null) {
                    try {
                        packet.packet.handle(handler);
                    } catch (CancelSendSignal ex) {
                        sendPacket = false;
                    }
                }
                if (sendPacket) {
                    handler.handle(packet);
                }
            } finally {
                packet.trySingleRelease();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            if (handler != null) {
                try {
                    handler.exception(cause);
                } catch (Exception ex) {
                }
            }

            ctx.close();
        }
    }
}
