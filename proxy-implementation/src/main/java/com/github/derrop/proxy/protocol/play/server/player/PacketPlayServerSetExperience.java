package com.github.derrop.proxy.protocol.play.server.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSetExperience implements Packet {

    private float currentXP;
    private int maxXP;
    private int level;

    public PacketPlayServerSetExperience(float currentXP, int maxXP, int level) {
        this.currentXP = currentXP;
        this.maxXP = maxXP;
        this.level = level;
    }

    public PacketPlayServerSetExperience() {
    }

    public float getCurrentXP() {
        return currentXP;
    }

    public void setCurrentXP(float currentXP) {
        this.currentXP = currentXP;
    }

    public int getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(int maxXP) {
        this.maxXP = maxXP;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.currentXP = protoBuf.readFloat();
        this.level = protoBuf.readVarInt();
        this.maxXP = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeFloat(this.currentXP);
        protoBuf.writeVarInt(this.level);
        protoBuf.writeVarInt(this.maxXP);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SET_EXPERIENCE;
    }
}
