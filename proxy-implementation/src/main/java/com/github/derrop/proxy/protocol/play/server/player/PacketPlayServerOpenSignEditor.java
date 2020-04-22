package com.github.derrop.proxy.protocol.play.server.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerOpenSignEditor implements Packet {

    private BlockPos pos;

    public PacketPlayServerOpenSignEditor(BlockPos pos) {
        this.pos = pos;
    }

    public PacketPlayServerOpenSignEditor() {
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.pos = protoBuf.readBlockPos();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeBlockPos(this.pos);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.OPEN_SIGN_EDITOR;
    }
}
