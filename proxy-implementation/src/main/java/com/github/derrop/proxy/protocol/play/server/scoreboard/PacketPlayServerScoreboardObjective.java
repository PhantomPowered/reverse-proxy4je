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
package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.criteria.ScoreObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardObjective implements Packet {

    private String name;
    private String value;
    private byte action;
    private ScoreObjectiveCriteria.RenderType type;

    public PacketPlayServerScoreboardObjective(String name) {
        this(name, null, null, (byte) 1);
    }

    public PacketPlayServerScoreboardObjective(String name, String value, ScoreObjectiveCriteria.RenderType type, byte action) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.action = action;
    }

    public PacketPlayServerScoreboardObjective() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.name = protoBuf.readString();
        this.action = protoBuf.readByte();

        if (action == 0 || action == 2) {
            this.value = protoBuf.readString();
            this.type = ScoreObjectiveCriteria.RenderType.valueOf(protoBuf.readString().toUpperCase());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.name);
        protoBuf.writeByte(this.action);

        if (action == 0 || action == 2) {
            protoBuf.writeString(this.value);
            protoBuf.writeString(this.type.toString().toLowerCase());
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_OBJECTIVE;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public ScoreObjectiveCriteria.RenderType getType() {
        return this.type;
    }

    public byte getAction() {
        return this.action;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(ScoreObjectiveCriteria.RenderType type) {
        this.type = type;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public String toString() {
        return "PacketPlayServerScoreboardObjective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + this.getType() + ", action=" + this.getAction() + ")";
    }
}
