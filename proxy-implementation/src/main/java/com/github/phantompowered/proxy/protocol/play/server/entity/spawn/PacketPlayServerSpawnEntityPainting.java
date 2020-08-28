package com.github.phantompowered.proxy.protocol.play.server.entity.spawn;

import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntityPainting implements Packet, EntityPacket {

    private int entityId;
    private Location location;
    private Facing facing;
    private String title;

    public PacketPlayServerSpawnEntityPainting(int entityId, Location location, Facing facing, String title) {
        this.entityId = entityId;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        this.location = protoBuf.readLocation();
        this.facing = Facing.getByHorizontalIndex(protoBuf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeString(this.title);
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte(this.facing.getHorizontalIndex());
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_PAINTING;
    }
}
