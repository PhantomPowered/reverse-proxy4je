package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateTileEntity implements Packet {

    private Location location;
    private int metadata;
    private NBTTagCompound nbt;

    public PacketPlayServerUpdateTileEntity(Location location, int metadata, NBTTagCompound nbt) {
        this.location = location;
        this.metadata = metadata;
        this.nbt = nbt;
    }

    public PacketPlayServerUpdateTileEntity() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        this.location = protoBuf.readLocation();
        this.metadata = protoBuf.readUnsignedByte();
        this.nbt = protoBuf.readNBTTagCompound();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte((byte) this.metadata);
        protoBuf.writeNBTTagCompound(this.nbt);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TILE_ENTITY_DATA;
    }
}
