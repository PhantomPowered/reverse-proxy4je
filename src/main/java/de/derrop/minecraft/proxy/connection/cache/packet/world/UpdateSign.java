package de.derrop.minecraft.proxy.connection.cache.packet.world;

import de.derrop.minecraft.proxy.util.BlockPos;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class UpdateSign extends DefinedPacket {

    private BlockPos pos;
    private BaseComponent[][] lines;

    @Override
    public void read(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.lines = new BaseComponent[4][];

        for (int i = 0; i < 4; i++) {
            this.lines[i] = ComponentSerializer.parse(readString(buf));
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());

        for (int i = 0; i < 4; i++) {
            writeString(ComponentSerializer.toString(this.lines[i]), buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
