package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateHealth implements Packet {

    private float health;
    private int foodLevel;
    private float saturationLevel;

    public PacketPlayServerUpdateHealth(float health, int foodLevel, float saturationLevel) {
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturationLevel = saturationLevel;
    }

    public PacketPlayServerUpdateHealth() {
    }

    public float getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturationLevel() {
        return saturationLevel;
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_HEALTH;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.health = protoBuf.readFloat();
        this.foodLevel = protoBuf.readVarInt();
        this.saturationLevel = protoBuf.readFloat();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeFloat(this.health);
        protoBuf.writeVarInt(this.foodLevel);
        protoBuf.writeFloat(this.saturationLevel);
    }

    public String toString() {
        return "PacketPlayServerUpdateHealth(health=" + this.health + ", foodLevel=" + this.foodLevel + ", saturationLevel=" + this.saturationLevel + ")";
    }
}
