package com.github.phantompowered.proxy.protocol.play.server.entity;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityUseBed implements Packet, EntityPacket {

    private int entityId;
    private Location bedLocation;

    public PacketPlayServerEntityUseBed(int entityId, Location bedLocation) {
        this.entityId = entityId;
        this.bedLocation = bedLocation;
    }

    public PacketPlayServerEntityUseBed() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.bedLocation = protoBuf.readLocation();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeLocation(this.bedLocation);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BED;
    }
}
