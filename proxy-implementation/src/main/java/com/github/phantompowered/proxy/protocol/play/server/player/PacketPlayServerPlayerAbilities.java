/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.protocol.play.server.player;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientPlayerAbilities;
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

    public PacketPlayClientPlayerAbilities toClient() {
        return new PacketPlayClientPlayerAbilities(this.invulnerable, this.flying, this.allowFlying, this.creativeMode, this.flySpeed, this.walkSpeed);
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
        byte b = 0;
        if (this.invulnerable) {
            b = (byte) (b | 1);
        }
        if (this.flying) {
            b = (byte) (b | 2);
        }
        if (this.allowFlying) {
            b = (byte) (b | 4);
        }
        if (this.creativeMode) {
            b = (byte) (b | 8);
        }

        protoBuf.writeByte(b);
        protoBuf.writeFloat(this.flySpeed);
        protoBuf.writeFloat(this.walkSpeed);
    }

    @Override
    public String toString() {
        return "PacketPlayServerPlayerAbilities{"
                + "invulnerable=" + invulnerable
                + ", flying=" + flying
                + ", allowFlying=" + allowFlying
                + ", creativeMode=" + creativeMode
                + ", flySpeed=" + flySpeed
                + ", walkSpeed=" + walkSpeed
                + '}';
    }
}
