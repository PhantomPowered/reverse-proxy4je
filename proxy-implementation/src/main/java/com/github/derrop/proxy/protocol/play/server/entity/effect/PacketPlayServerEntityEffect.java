package com.github.derrop.proxy.protocol.play.server.entity.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityEffect implements Packet {

    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte hideParticles;

    public PacketPlayServerEntityEffect(int entityId, byte effectId, byte amplifier, int duration, byte hideParticles) {
        this.entityId = entityId;
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.duration = duration;
        this.hideParticles = hideParticles;
    }

    public PacketPlayServerEntityEffect() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_EFFECT;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.effectId = protoBuf.readByte();
        this.amplifier = protoBuf.readByte();
        this.duration = protoBuf.readVarInt();
        this.hideParticles = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.effectId);
        protoBuf.writeByte(this.amplifier);
        protoBuf.writeVarInt(this.duration);
        protoBuf.writeByte(this.hideParticles);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public byte getHideParticles() {
        return this.hideParticles;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String toString() {
        return "PacketPlayServerEntityEffect(entityId=" + this.getEntityId() + ", effectId=" + this.getEffectId() + ", amplifier=" + this.getAmplifier() + ", duration=" + this.getDuration() + ", hideParticles=" + this.getHideParticles() + ")";
    }
}
