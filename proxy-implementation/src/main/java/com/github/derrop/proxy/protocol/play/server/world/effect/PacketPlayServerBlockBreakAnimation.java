package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockBreakAnimation implements Packet {

    private int breakerId;
    private Location location;
    private int progress;

    public PacketPlayServerBlockBreakAnimation(int breakerId, Location location, int progress) {
        this.breakerId = breakerId;
        this.location = location;
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
        this.breakerId = protoBuf.readVarInt();
        this.location = protoBuf.readLocation();
        this.progress = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.breakerId);
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte(this.progress);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_BREAK_ANIMATION;
    }
}
