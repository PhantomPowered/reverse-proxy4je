package com.github.phantompowered.proxy.protocol.play.client.entity;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientUseEntity implements Packet, EntityPacket {

    private int entityId;
    private Action action;
    private Vector hitVector;

    public PacketPlayClientUseEntity(int entityId, Action action, Vector hitVector) {
        this.entityId = entityId;
        this.action = action;
        this.hitVector = hitVector;
    }

    public PacketPlayClientUseEntity() {
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

    public Vector getHitVector() {
        return hitVector;
    }

    public void setHitVector(Vector hitVector) {
        this.hitVector = hitVector;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.action = Action.values()[protoBuf.readVarInt()];

        if (this.action == Action.INTERACT_AT) {
            this.hitVector = new Vector(protoBuf.readFloat(), protoBuf.readFloat(), protoBuf.readFloat());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeVarInt(this.action.ordinal());

        if (this.action == Action.INTERACT_AT) {
            protoBuf.writeFloat((float) this.hitVector.getX());
            protoBuf.writeFloat((float) this.hitVector.getY());
            protoBuf.writeFloat((float) this.hitVector.getZ());
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.USE_ENTITY;
    }

    @Override
    public String toString() {
        return "PacketPlayClientUseEntity{"
                + "entityId=" + entityId
                + ", action=" + action
                + ", hitVector=" + hitVector
                + '}';
    }

    public enum Action {
        INTERACT,
        ATTACK,
        INTERACT_AT,
        ;
    }

}
