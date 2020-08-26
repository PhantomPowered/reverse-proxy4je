package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientPlayerDigging implements Packet {

    private Location location;
    private Facing facing;
    private Action action;

    public PacketPlayClientPlayerDigging(Location location, Facing facing, Action action) {
        this.location = location;
        this.facing = facing;
        this.action = action;
    }

    public PacketPlayClientPlayerDigging() {
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[protoBuf.readVarInt()];
        this.location = protoBuf.readLocation();
        this.facing = Facing.getFront(protoBuf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.action.ordinal());
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte(this.facing.getIndex());
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.BLOCK_DIG;
    }

    public enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        ;
    }

}
