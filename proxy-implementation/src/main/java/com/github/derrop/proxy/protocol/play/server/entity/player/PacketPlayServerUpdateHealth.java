package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayServerUpdateHealth extends DefinedPacket {

    private float health;
    private int foodLevel;
    private float saturationLevel;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.health = buf.readFloat();
        this.foodLevel = readVarInt(buf);
        this.saturationLevel = buf.readFloat();
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeFloat(this.health);
        writeVarInt(this.foodLevel, buf);
        buf.writeFloat(this.saturationLevel);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_HEALTH;
    }
}
