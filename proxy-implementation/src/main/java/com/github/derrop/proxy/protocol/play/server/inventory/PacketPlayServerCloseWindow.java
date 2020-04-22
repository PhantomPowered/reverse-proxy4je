package com.github.derrop.proxy.protocol.play.server.inventory;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import net.kyori.text.Component;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerCloseWindow implements Packet {

    private int windowId;

    public PacketPlayServerCloseWindow(int windowId) {
        this.windowId = windowId;
    }

    public PacketPlayServerCloseWindow() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CLOSE_WINDOW;
    }
}
