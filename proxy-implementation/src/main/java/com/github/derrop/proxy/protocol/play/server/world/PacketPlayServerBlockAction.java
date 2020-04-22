package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockAction implements Packet {

    private BlockPos pos;
    private int instrument;
    private int pitch;
    private Material material;

    public PacketPlayServerBlockAction(BlockPos pos, int instrument, int pitch, Material material) {
        this.pos = pos;
        this.instrument = instrument;
        this.pitch = pitch;
        this.material = material;
    }

    public PacketPlayServerBlockAction() {
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.pos = protoBuf.readBlockPos();
        this.instrument = protoBuf.readUnsignedByte();
        this.pitch = protoBuf.readUnsignedByte();
        this.material = Material.getMaterial(protoBuf.readVarInt() & 4095);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeBlockPos(this.pos);
        protoBuf.writeByte(this.instrument);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeVarInt(this.material.getId() & 4095);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_ACTION;
    }
}
