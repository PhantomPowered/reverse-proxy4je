package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerEntityStatus extends DefinedPacket {

    public static final byte DEBUG_INFO_REDUCED = 22;
    public static final byte DEBUG_INFO_NORMAL = 23;
    //
    private int entityId;
    private byte status;

    @Override
    public void read(@NotNull ByteBuf buf) {
        entityId = buf.readInt();
        status = buf.readByte();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(status);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_STATUS;
    }
}
