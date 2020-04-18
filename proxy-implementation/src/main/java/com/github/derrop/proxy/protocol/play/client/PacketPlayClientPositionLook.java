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
package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class PacketPlayClientPositionLook implements Packet {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<EnumFlags> flags;

    public PacketPlayClientPositionLook(Location location, Set<EnumFlags> flags) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = PlayerPositionPacketUtil.getFixLocation(location.getYaw());
        this.pitch = PlayerPositionPacketUtil.getFixLocation(location.getPitch());
        this.flags = flags;
    }

    public PacketPlayClientPositionLook() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.POSITION_LOOK;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.x = protoBuf.readDouble();
        this.y = protoBuf.readDouble();
        this.z = protoBuf.readDouble();
        this.yaw = protoBuf.readFloat();
        this.pitch = protoBuf.readFloat();
        this.flags = EnumFlags.read(protoBuf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeDouble(this.x);
        protoBuf.writeDouble(this.y);
        protoBuf.writeDouble(this.z);
        protoBuf.writeFloat(this.yaw);
        protoBuf.writeFloat(this.pitch);
        protoBuf.writeByte(EnumFlags.write(this.flags));
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Set<EnumFlags> getFlags() {
        return this.flags;
    }

    public enum EnumFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int id;

        EnumFlags(int id) {
            this.id = id;
        }

        private int id() {
            return 1 << this.id;
        }

        private boolean shouldAccept(int full) {
            return (full & this.id()) == this.id();
        }

        @NotNull
        public static Set<EnumFlags> read(int full) {
            Set<EnumFlags> set = EnumSet.noneOf(EnumFlags.class);
            for (EnumFlags flags : values()) {
                if (flags.shouldAccept(full)) {
                    set.add(flags);
                }
            }

            return set;
        }

        public static int write(@NotNull Set<EnumFlags> flags) {
            int i = 0;
            for (EnumFlags values : flags) {
                i |= values.id();
            }

            return i;
        }
    }
}
