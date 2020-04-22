package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockBreakAnimation implements Packet {

    private int breakerId;
    private BlockPos pos;
    private int progress;

    public PacketPlayServerBlockBreakAnimation(int breakerId, BlockPos pos, int progress) {
        this.breakerId = breakerId;
        this.pos = pos;
        this.progress = progress;
    }

    public PacketPlayServerBlockBreakAnimation() {
    }

    public int getBreakerId() {
        return breakerId;
    }

    public void setBreakerId(int breakerId) {
        this.breakerId = breakerId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.breakerId = protoBuf.readVarInt();
        this.pos = BlockPos.fromLong(protoBuf.readLong());
        this.progress = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.breakerId);
        protoBuf.writeLong(this.pos.toLong());
        protoBuf.writeByte(this.progress);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_BREAK_ANIMATION;
    }
}
