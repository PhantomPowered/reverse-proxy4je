package com.github.phantompowered.proxy.protocol.play.server.inventory;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerConfirmTransaction implements Packet {

    private int windowId;
    private short actionNumber;
    private boolean noResponse; // when false, the client sends the ConfirmTransaction Packet back to the server

    public PacketPlayServerConfirmTransaction() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public short getActionNumber() {
        return actionNumber;
    }

    public void setActionNumber(short actionNumber) {
        this.actionNumber = actionNumber;
    }

    public boolean isNoResponse() {
        return noResponse;
    }

    public void setNoResponse(boolean noResponse) {
        this.noResponse = noResponse;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readUnsignedByte();
        this.actionNumber = protoBuf.readShort();
        this.noResponse = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
        protoBuf.writeShort(this.actionNumber);
        protoBuf.writeBoolean(this.noResponse);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TRANSACTION;
    }
}
