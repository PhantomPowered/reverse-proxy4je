package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientBlockPlace implements Packet {

    private Location location;
    private int placedBlockDirection;
    private ItemStack stack;
    private float facingX;
    private float facingY;
    private float facingZ;

    public PacketPlayClientBlockPlace(Location location, int placedBlockDirection, ItemStack stack, float facingX, float facingY, float facingZ) {
        this.location = location;
        this.placedBlockDirection = placedBlockDirection;
        this.stack = stack;
        this.facingX = facingX;
        this.facingY = facingY;
        this.facingZ = facingZ;
    }

    public PacketPlayClientBlockPlace() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getPlacedBlockDirection() {
        return placedBlockDirection;
    }

    public void setPlacedBlockDirection(int placedBlockDirection) {
        this.placedBlockDirection = placedBlockDirection;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public float getFacingX() {
        return facingX;
    }

    public void setFacingX(float facingX) {
        this.facingX = facingX;
    }

    public float getFacingY() {
        return facingY;
    }

    public void setFacingY(float facingY) {
        this.facingY = facingY;
    }

    public float getFacingZ() {
        return facingZ;
    }

    public void setFacingZ(float facingZ) {
        this.facingZ = facingZ;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.location = buf.readLocation();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStack();
        this.facingX = (float) buf.readUnsignedByte() / 16.0F;
        this.facingY = (float) buf.readUnsignedByte() / 16.0F;
        this.facingZ = (float) buf.readUnsignedByte() / 16.0F;
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeLocation(this.location);
        buf.writeByte(this.placedBlockDirection);
        buf.writeItemStack(this.stack);
        buf.writeByte((int) (this.facingX * 16.0F));
        buf.writeByte((int) (this.facingY * 16.0F));
        buf.writeByte((int) (this.facingZ * 16.0F));
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.BLOCK_PLACE;
    }

    @Override
    public String toString() {
        return "PacketPlayClientBlockPlace{"
                + "location=" + location
                + ", placedBlockDirection=" + placedBlockDirection
                + ", stack=" + stack
                + ", facingX=" + facingX
                + ", facingY=" + facingY
                + ", facingZ=" + facingZ
                + '}';
    }
}
