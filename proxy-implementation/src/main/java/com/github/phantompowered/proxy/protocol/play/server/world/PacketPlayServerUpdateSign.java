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
package com.github.phantompowered.proxy.protocol.play.server.world;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateSign implements Packet {

    private Location location;
    private Component[] lines;

    public PacketPlayServerUpdateSign(Location location, Component[] lines) {
        this.location = location;
        this.lines = lines;
    }

    public PacketPlayServerUpdateSign() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_SIGN;
    }

    public Location getLocation() {
        return this.location;
    }

    public Component[] getLines() {
        return this.lines;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.location = protoBuf.readLocation();
        this.lines = new Component[4];

        for (int i = 0; i < 4; i++) {
            this.lines[i] = GsonComponentSerializer.gson().deserialize(protoBuf.readString());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLocation(this.location);

        for (int i = 0; i < 4; i++) {
            protoBuf.writeString(GsonComponentSerializer.gson().serialize(this.lines[i]));
        }
    }

    public String toString() {
        return "PacketPlayServerUpdateSign(pos=" + this.getLocation() + ", lines=" + java.util.Arrays.deepToString(this.getLines()) + ")";
    }
}
