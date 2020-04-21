package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityUseBed implements Packet {

    private int entityId;
    private BlockPos bedPos;

    public PacketPlayServerEntityUseBed(int entityId, BlockPos bedPos) {
        this.entityId = entityId;
        this.bedPos = bedPos;
    }

    public PacketPlayServerEntityUseBed() {
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public BlockPos getBedPos() {
        return bedPos;
    }

    public void setBedPos(BlockPos bedPos) {
        this.bedPos = bedPos;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.bedPos = BlockPos.fromLong(protoBuf.readLong());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeLong(this.bedPos.toLong());
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BED;
    }
}
