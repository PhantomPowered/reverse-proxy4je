package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateTileEntity implements Packet {

    private BlockPos pos;
    private int metadata;
    private NBTTagCompound nbt;

    public PacketPlayServerUpdateTileEntity(BlockPos pos, int metadata, NBTTagCompound nbt) {
        this.pos = pos;
        this.metadata = metadata;
        this.nbt = nbt;
    }

    public PacketPlayServerUpdateTileEntity() {
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public int getMetadata() {
        return metadata;
    }

    public void setMetadata(int metadata) {
        this.metadata = metadata;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public void setNbt(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.pos = BlockPos.fromLong(protoBuf.readLong());
        this.metadata = protoBuf.readUnsignedByte();
        this.nbt = PacketUtil.readNBTTagCompound(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.pos.toLong());
        protoBuf.writeByte((byte) this.metadata);
        PacketUtil.writeNBTTagCompound(protoBuf, nbt);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TILE_ENTITY_DATA;
    }
}
