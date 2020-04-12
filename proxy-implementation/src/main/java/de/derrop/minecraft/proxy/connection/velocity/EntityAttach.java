package de.derrop.minecraft.proxy.connection.velocity;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class EntityAttach extends DefinedPacket {

    private int leash;
    private int entityId;
    private int vehicleEntityId;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.vehicleEntityId = buf.readInt();
        this.leash = buf.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.vehicleEntityId);
        buf.writeByte(this.leash);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
