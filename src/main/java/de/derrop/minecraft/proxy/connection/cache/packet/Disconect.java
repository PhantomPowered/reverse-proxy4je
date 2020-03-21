package de.derrop.minecraft.proxy.connection.cache.packet;

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
public class Disconect extends DefinedPacket {

    private BaseComponent[] reason;

    @Override
    public void read(ByteBuf buf) {
        this.reason = ComponentSerializer.parse(readString(buf));
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(ComponentSerializer.toString(this.reason), buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
