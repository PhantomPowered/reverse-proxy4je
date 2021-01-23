package com.github.phantompowered.proxy.protocol.play.server.world.effect;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockBreakAnimation implements Packet, EntityPacket {

    private int entityId;
    private Location location;
    private int progress;

    public PacketPlayServerBlockBreakAnimation(int entityId, Location location, int progress) {
        this.entityId = entityId;
        this.location = location;
        this.progress = progress;
    }

    public PacketPlayServerBlockBreakAnimation() {
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.location = protoBuf.readLocation();
        this.progress = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte(this.progress);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_BREAK_ANIMATION;
    }
}
