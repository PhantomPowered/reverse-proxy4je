package com.github.derrop.proxy.protocol.play.server.entity.player;

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
public class PacketPlayServerGameStateChange extends DefinedPacket {

    private int state;
    private float value;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.state = buf.readUnsignedByte();
        this.value = buf.readFloat();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeByte(this.state);
        buf.writeFloat(this.value);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ClientBound.Play.GAME_STATE_CHANGE;
    }
}
