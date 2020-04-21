package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.block.Facing;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntityPainting implements Packet, EntityPacket {

    private int entityId;
    private BlockPos pos;
    private Facing facing;
    private String title;

    public PacketPlayServerSpawnEntityPainting(int entityId, BlockPos pos, Facing facing, String title) {
        this.entityId = entityId;
        this.pos = pos;
        this.facing = facing;
        this.title = title;
    }

    public PacketPlayServerSpawnEntityPainting() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Facing getFacing() {
        return facing;
    }

    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.title = protoBuf.readString();
        this.pos = BlockPos.fromLong(protoBuf.readLong());
        this.facing = Facing.getHorizontal(protoBuf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeString(this.title);
        protoBuf.writeLong(this.pos.toLong());
        protoBuf.writeByte(this.facing.getHorizontalIndex());
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_PAINTING;
    }
}
