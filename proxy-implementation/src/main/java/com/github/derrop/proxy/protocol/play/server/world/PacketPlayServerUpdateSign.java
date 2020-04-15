package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerUpdateSign extends DefinedPacket {

    private BlockPos pos;
    private BaseComponent[][] lines;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.lines = new BaseComponent[4][];

        for (int i = 0; i < 4; i++) {
            this.lines[i] = ComponentSerializer.parse(readString(buf));
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeLong(this.pos.toLong());

        for (int i = 0; i < 4; i++) {
            writeString(ComponentSerializer.toString(this.lines[i]), buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_SIGN;
    }
}
