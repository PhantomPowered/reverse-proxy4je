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
package com.github.derrop.proxy.protocol.play.server.player.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class PacketPlayServerPosition implements Packet {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private byte flags;

    public PacketPlayServerPosition(double x, double y, double z, float yaw, float pitch, byte flags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
    }

    public PacketPlayServerPosition(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0);
    }

    public PacketPlayServerPosition() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.POSITION;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.x = protoBuf.readDouble();
        this.y = protoBuf.readDouble();
        this.z = protoBuf.readDouble();
        this.yaw = protoBuf.readFloat();
        this.pitch = protoBuf.readFloat();
        this.flags = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeDouble(this.x);
        protoBuf.writeDouble(this.y);
        protoBuf.writeDouble(this.z);
        protoBuf.writeFloat(this.yaw);
        protoBuf.writeFloat(this.pitch);
        protoBuf.writeByte(this.flags);
    }

    @Override
    public String toString() {
        return "PacketPlayServerPosition{"
                + "x=" + x
                + ", y=" + y
                + ", z=" + z
                + ", yaw=" + yaw
                + ", pitch=" + pitch
                + ", flags=" + flags
                + '}';
    }

    public enum TeleportFlags {

        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int f;

        private TeleportFlags(int var3) {
            this.f = var3;
        }

        private int getDefaultValue() {
            return 1 << this.f;
        }

        private boolean matches(int var1) {
            return (var1 & this.getDefaultValue()) == this.getDefaultValue();
        }

        public static Set<TeleportFlags> deserialize(int in) {
            EnumSet<TeleportFlags> result = EnumSet.noneOf(TeleportFlags.class);
            TeleportFlags[] values = values();

            for (int i = 0; i < values.length; ++i) {
                TeleportFlags flags = values[i];
                if (flags.matches(in)) {
                    result.add(flags);
                }
            }

            return result;
        }

        public static int serialize(Set<TeleportFlags> in) {
            int result = 0;

            TeleportFlags var3;
            for (TeleportFlags flags : in) {
                result |= flags.getDefaultValue();
            }

            return result;
        }
    }
}
