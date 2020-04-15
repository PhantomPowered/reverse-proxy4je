package com.github.derrop.proxy.connection.velocity;

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
public class PlayerPosition extends DefinedPacket {

    private double x;
    private double y;
    private double z;
    private boolean onGround;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.onGround = buf.readUnsignedByte() != 0;
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.onGround ? 1 : 0);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
