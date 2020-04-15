package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerEntityDestroy extends DefinedPacket {

    private int[] entityIds;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.entityIds = new int[readVarInt(buf)];
        for (int i = 0; i < this.entityIds.length; i++) {
            this.entityIds[i] = readVarInt(buf);
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.entityIds.length, buf);
        for (int entityId : this.entityIds) {
            writeVarInt(entityId, buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_DESTROY;
    }
}
