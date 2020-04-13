package com.github.derrop.proxy.connection.cache.packet.entity.player;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHealth extends DefinedPacket {

    private float health;
    private int foodLevel;
    private float saturationLevel;

    @Override
    public void read(ByteBuf buf) {
        this.health = buf.readFloat();
        this.foodLevel = readVarInt(buf);
        this.saturationLevel = buf.readFloat();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeFloat(this.health);
        writeVarInt(this.foodLevel, buf);
        buf.writeFloat(this.saturationLevel);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
