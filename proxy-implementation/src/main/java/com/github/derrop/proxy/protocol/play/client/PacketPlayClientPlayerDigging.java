package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.block.Facing;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientPlayerDigging implements Packet {

    private BlockPos pos;
    private Facing facing;
    private Action action;

    public PacketPlayClientPlayerDigging(BlockPos pos, Facing facing, Action action) {
        this.pos = pos;
        this.facing = facing;
        this.action = action;
    }

    public PacketPlayClientPlayerDigging() {
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[protoBuf.readVarInt()];
        this.pos = protoBuf.readBlockPos();
        this.facing = Facing.getFront(protoBuf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.action.ordinal());
        protoBuf.writeBlockPos(this.pos);
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
