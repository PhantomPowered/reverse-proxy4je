package com.github.derrop.proxy.protocol.play.server.entity.effect;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerRemoveEntityEffect extends DefinedPacket {

    private int entityId;
    private int effectId;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.effectId = buf.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.effectId);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
