package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateEntityNBT implements Packet, EntityPacket {

    private int entityId;
    private NBTTagCompound nbt;

    public PacketPlayServerUpdateEntityNBT(int entityId, NBTTagCompound nbt) {
        this.entityId = entityId;
        this.nbt = nbt;
    }

    public PacketPlayServerUpdateEntityNBT() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public void setNbt(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.nbt = protoBuf.readNBTTagCompound();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeNBTTagCompound(this.nbt);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_ENTITY_NBT;
    }
}
