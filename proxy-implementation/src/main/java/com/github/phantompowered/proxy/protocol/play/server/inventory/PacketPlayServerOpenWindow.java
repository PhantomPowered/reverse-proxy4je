package com.github.phantompowered.proxy.protocol.play.server.inventory;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerOpenWindow implements Packet, EntityPacket {

    private int windowId;
    private String inventoryType;
    private Component windowTitle;
    private int slotCount;
    private int entityId; // only necessary when the type is "EntityHorse"

    public PacketPlayServerOpenWindow(int windowId, String inventoryType, Component windowTitle, int slotCount, int entityId) {
        this.windowId = windowId;
        this.inventoryType = inventoryType;
        this.windowTitle = windowTitle;
        this.slotCount = slotCount;
        this.entityId = entityId;
    }

    public PacketPlayServerOpenWindow() {
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public Component getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(Component windowTitle) {
        this.windowTitle = windowTitle;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = slotCount;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.windowId = protoBuf.readUnsignedByte();
        this.inventoryType = protoBuf.readString();
        this.windowTitle = GsonComponentSerializer.gson().deserialize(protoBuf.readString());
        this.slotCount = protoBuf.readUnsignedByte();

        if (this.inventoryType.equals("EntityHorse")) {
            this.entityId = protoBuf.readInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.windowId);
        protoBuf.writeString(this.inventoryType);
        protoBuf.writeString(GsonComponentSerializer.gson().serialize(this.windowTitle));
        protoBuf.writeByte(this.slotCount);

        if (this.inventoryType.equals("EntityHorse")) {
            protoBuf.writeInt(this.entityId);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.OPEN_WINDOW;
    }
}
