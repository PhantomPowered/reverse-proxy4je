package com.github.derrop.proxy.network.minecraft;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final ProtocolDirection direction;

    private ProtocolState protocolState;

    public MinecraftDecoder(ProtocolDirection direction, ProtocolState protocolState) {
        this.direction = direction;
        this.protocolState = protocolState;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        ByteBuf copy = byteBuf.copy();

        int id = ByteBufUtils.readVarInt(copy);
        Packet packet = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PacketRegistry.class).getPacket(this.direction, this.protocolState, id);
        if (packet == null) {
            byteBuf.skipBytes(byteBuf.readableBytes());
            return;
        }

        packet.read(byteBuf);
        list.add(new DecodedPacket(copy, packet));
    }

    public void setProtocolState(@NotNull ProtocolState protocolState) {
        this.protocolState = protocolState;
    }

    @NotNull
    public ProtocolState getProtocolState() {
        return protocolState;
    }
}
