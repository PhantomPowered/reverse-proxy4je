package com.github.phantompowered.proxy.protocol.play.server.world;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerBlockAction implements Packet {

    private Location location;
    private int instrument;
    private int pitch;
    private Material material;

    public PacketPlayServerBlockAction(Location location, int instrument, int pitch, Material material) {
        this.location = location;
        this.instrument = instrument;
        this.pitch = pitch;
        this.material = material;
    }

    public PacketPlayServerBlockAction() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        this.location = protoBuf.readLocation();
        this.instrument = protoBuf.readUnsignedByte();
        this.pitch = protoBuf.readUnsignedByte();
        this.material = Material.getMaterial(protoBuf.readVarInt() & 4095);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLocation(this.location);
        protoBuf.writeByte(this.instrument);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeVarInt(this.material.getId() & 4095);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.BLOCK_ACTION;
    }
}
