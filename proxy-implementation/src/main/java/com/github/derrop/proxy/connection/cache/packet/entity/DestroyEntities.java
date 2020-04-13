package com.github.derrop.proxy.connection.cache.packet.entity;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class DestroyEntities extends DefinedPacket {

    private int[] entityIds;

    @Override
    public void read(ByteBuf buf) {
        this.entityIds = new int[readVarInt(buf)];
        for (int i = 0; i < this.entityIds.length; i++) {
            this.entityIds[i] = readVarInt(buf);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityIds.length, buf);
        for (int entityId : this.entityIds) {
            writeVarInt(entityId, buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
