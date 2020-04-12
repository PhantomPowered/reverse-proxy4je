package de.derrop.minecraft.proxy.connection.cache.packet.entity.effect;

import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class EntityEffect extends DefinedPacket {

    private int entityId;
    private byte effectId;
    private byte amplifier;
    /**
     * The duration in ticks (20 ticks = 1 second)
     */
    private int duration;
    private byte hideParticles;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.effectId = buf.readByte();
        this.amplifier = buf.readByte();
        this.duration = readVarInt(buf);
        this.hideParticles = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        writeVarInt(this.duration, buf);
        buf.writeByte(this.hideParticles);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
