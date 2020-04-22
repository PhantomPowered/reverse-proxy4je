package com.github.derrop.proxy.protocol.play.client.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.Vec3;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientUseEntity implements Packet, EntityPacket {

    private int entityId;
    private Action action;
    private Vec3 hitVector;

    public PacketPlayClientUseEntity(int entityId, Action action, Vec3 hitVector) {
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

    public Vec3 getHitVector() {
        return hitVector;
    }

    public void setHitVector(Vec3 hitVector) {
        this.hitVector = hitVector;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.action = Action.values()[protoBuf.readVarInt()];

        if (this.action == Action.INTERACT_AT) {
            this.hitVector = new Vec3(protoBuf.readFloat(), protoBuf.readFloat(), protoBuf.readFloat());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeVarInt(this.action.ordinal());

        if (this.action == Action.INTERACT_AT) {
            protoBuf.writeFloat((float) this.hitVector.xCoord);
            protoBuf.writeFloat((float) this.hitVector.yCoord);
            protoBuf.writeFloat((float) this.hitVector.zCoord);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.USE_ENTITY;
    }

    public enum Action {
        INTERACT,
        ATTACK,
        INTERACT_AT,
        ;
    }

}
