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
package com.github.derrop.proxy.network.pipeline.minecraft;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.network.wrapper.DefaultProtoBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final ServiceRegistry registry;
    private final ProtocolDirection direction;

    private ProtocolState protocolState;

    public MinecraftDecoder(ServiceRegistry registry, ProtocolDirection direction, ProtocolState protocolState) {
        this.registry = registry;
        this.direction = direction;
        this.protocolState = protocolState;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        ByteBuf copy = byteBuf.copy();

        DefaultProtoBuf protoBuf = new DefaultProtoBuf(47, byteBuf);
        Packet packet = this.registry.getProviderUnchecked(PacketRegistry.class).getPacket(this.direction, this.protocolState, protoBuf.readVarInt());
        if (packet == null) {
            list.add(new DecodedPacket(new DefaultProtoBuf(47, copy), null));
            return;
        }

        packet.read(protoBuf, this.direction, protoBuf.getProtocolVersion());
        list.add(new DecodedPacket(new DefaultProtoBuf(47, copy), packet));
    }

    public void setProtocolState(@NotNull ProtocolState protocolState) {
        this.protocolState = protocolState;
    }

    @NotNull
    public ProtocolState getProtocolState() {
        return protocolState;
    }

    @NotNull
    public ProtocolDirection getDirection() {
        return direction;
    }
}
