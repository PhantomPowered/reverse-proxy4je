package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockChange implements Packet {

    private BlockPos pos;
    private int blockState;

    public PacketPlayServerBlockChange(BlockPos pos, int blockState) {
        this.pos = pos;
        this.blockState = blockState;
    }

    public PacketPlayServerBlockChange() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_CHANGE;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getBlockState() {
        return this.blockState;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.pos = BlockPos.fromLong(protoBuf.readLong());
        this.blockState = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.pos.toLong());
        protoBuf.writeVarInt(this.blockState);
    }

    public String toString() {
        return "PacketPlayServerBlockChange(pos=" + this.getPos() + ", blockState=" + this.getBlockState() + ")";
    }
}
