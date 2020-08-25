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
package com.github.derrop.proxy.protocol.play.client.position;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketPlayClientPosition extends PacketPlayClientPlayerPosition {

    private double x;
    private double y;
    private double z;

    public PacketPlayClientPosition(Location location) {
        super(location.isOnGround());
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public PacketPlayClientPosition() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.POSITION;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.x = protoBuf.readDouble();
        this.y = protoBuf.readDouble();
        this.z = protoBuf.readDouble();
        super.read(protoBuf, direction, protocolVersion);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeDouble(this.x);
        protoBuf.writeDouble(this.y);
        protoBuf.writeDouble(this.z);
        super.write(protoBuf, direction, protocolVersion);
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

    @Override
    public Location getLocation(@Nullable Location before) {
        return new Location(this.x, this.y, this.z, before != null ? before.getYaw() : -1, before != null ? before.getPitch() : -1, super.isOnGround());
    }

    @Override
    public String toString() {
        return "PacketPlayClientPosition{"
                + "x=" + x
                + ", y=" + y
                + ", z=" + z
                + "} " + super.toString();
    }
}
