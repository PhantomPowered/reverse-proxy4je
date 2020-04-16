package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerPlayerAbilities implements Packet {

    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public PacketPlayServerPlayerAbilities(boolean invulnerable, boolean flying, boolean allowFlying, boolean creativeMode, float flySpeed, float walkSpeed) {
        this.invulnerable = invulnerable;
        this.flying = flying;
        this.allowFlying = allowFlying;
        this.creativeMode = creativeMode;
        this.flySpeed = flySpeed;
        this.walkSpeed = walkSpeed;
    }

    public PacketPlayServerPlayerAbilities() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ABILITIES;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean isAllowFlying() {
        return this.allowFlying;
    }

    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        byte b = protoBuf.readByte();

        this.invulnerable = (b & 1) > 0;
        this.flying = (b & 2) > 0;
        this.allowFlying = (b & 4) > 0;
        this.creativeMode = (b & 8) > 0;
        this.flySpeed = protoBuf.readFloat();
        this.walkSpeed = protoBuf.readFloat();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        byte b0 = 0;

        if (this.isInvulnerable()) {
            b0 = (byte) (b0 | 1);
        }

        if (this.isFlying()) {
            b0 = (byte) (b0 | 2);
        }

        if (this.isAllowFlying()) {
            b0 = (byte) (b0 | 4);
        }

        if (this.isCreativeMode()) {
            b0 = (byte) (b0 | 8);
        }

        protoBuf.writeByte(b0);
        protoBuf.writeFloat(this.flySpeed);
        protoBuf.writeFloat(this.walkSpeed);
    }

    public String toString() {
        return "PacketPlayServerPlayerAbilities(invulnerable=" + this.isInvulnerable() + ", flying=" + this.isFlying() + ", allowFlying=" + this.isAllowFlying() + ", creativeMode=" + this.isCreativeMode() + ", flySpeed=" + this.getFlySpeed() + ", walkSpeed=" + this.getWalkSpeed() + ")";
    }
}
