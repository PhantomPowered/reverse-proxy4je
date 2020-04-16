package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnPosition implements Packet {

    private BlockPos spawnPosition;

    public PacketPlayServerSpawnPosition(BlockPos spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public PacketPlayServerSpawnPosition() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_POSITION;
    }

    public BlockPos getSpawnPosition() {
        return this.spawnPosition;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.spawnPosition = BlockPos.fromLong(protoBuf.readLong());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.spawnPosition.toLong());
    }

    public String toString() {
        return "PacketPlayServerSpawnPosition(spawnPosition=" + this.getSpawnPosition() + ")";
    }
}
