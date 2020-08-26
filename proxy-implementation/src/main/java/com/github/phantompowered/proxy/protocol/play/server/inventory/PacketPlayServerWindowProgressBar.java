package com.github.phantompowered.proxy.protocol.play.server.inventory;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWindowProgressBar implements Packet {

    private int windowId;
    private int varIndex;
    private int varValue;

    public PacketPlayServerWindowProgressBar(int windowId, int varIndex, int varValue) {
        this.windowId = windowId;
        this.varIndex = varIndex;
        this.varValue = varValue;
    }

    public PacketPlayServerWindowProgressBar() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public int getVarIndex() {
        return varIndex;
    }

    public void setVarIndex(int varIndex) {
        this.varIndex = varIndex;
    }

    public int getVarValue() {
        return varValue;
    }

    public void setVarValue(int varValue) {
        this.varValue = varValue;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readUnsignedByte();
        this.varIndex = protoBuf.readShort();
        this.varValue = protoBuf.readShort();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
        protoBuf.writeShort(this.varIndex);
        protoBuf.writeShort(this.varValue);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WINDOW_PROGRESS_BAR;
    }
}
