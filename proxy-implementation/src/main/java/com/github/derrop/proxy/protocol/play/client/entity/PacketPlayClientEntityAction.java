package com.github.derrop.proxy.protocol.play.client.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.EntityPacket;
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

    public int getEntityId() {
        return entityId;
    }

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
