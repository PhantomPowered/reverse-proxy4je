package com.github.derrop.proxy.connection.cache.packet.entity.player;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Camera extends DefinedPacket {

    private int entityId;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
