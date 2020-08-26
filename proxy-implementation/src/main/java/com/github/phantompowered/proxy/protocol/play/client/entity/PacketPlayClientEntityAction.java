package com.github.phantompowered.proxy.protocol.play.client.entity;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientEntityAction implements Packet, EntityPacket {

    private int entityId;
    private Action action;
    private int auxData;

    public PacketPlayClientEntityAction(int entityId, Action action) {
        this(entityId, action, 0);
    }

    public PacketPlayClientEntityAction(int entityId, Action action, int auxData) {
        this.entityId = entityId;
        this.action = action;
        this.auxData = auxData;
    }

    public PacketPlayClientEntityAction() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getAuxData() {
        return auxData;
    }

    public void setAuxData(int auxData) {
        this.auxData = auxData;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = buf.readVarInt();
        this.action = Action.values()[buf.readVarInt()];
        this.auxData = buf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.action.ordinal());
        buf.writeVarInt(this.auxData);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.ENTITY_ACTION;
    }

    @Override
    public String toString() {
        return "PacketPlayClientEntityAction{"
                + "entityId=" + entityId
                + ", action=" + action
                + ", auxData=" + auxData
                + '}';
    }

    public enum Action {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        RIDING_JUMP,
        OPEN_INVENTORY,
        ;
    }

}
