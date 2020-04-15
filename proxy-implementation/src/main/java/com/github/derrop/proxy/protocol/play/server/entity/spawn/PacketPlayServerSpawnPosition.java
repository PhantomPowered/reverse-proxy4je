package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
@AllArgsConstructor
public class PacketPlayServerSpawnPosition extends DefinedPacket {

    private BlockPos spawnPosition;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.spawnPosition = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeLong(this.spawnPosition.toLong());
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_POSITION;
    }
}
