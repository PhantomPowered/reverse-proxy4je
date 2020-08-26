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
package com.github.phantompowered.proxy.protocol.play.server.scoreboard;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PacketPlayServerScoreboardTeam implements Packet {

    private String name;
    private byte mode;
    private String displayName;
    private String prefix;
    private String suffix;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private byte friendlyFire;
    private String[] players;

    public PacketPlayServerScoreboardTeam(String name) {
        this.name = name;
        this.mode = 1;
    }

    public PacketPlayServerScoreboardTeam(String name, byte mode, String displayName, String prefix, String suffix, String nameTagVisibility, String collisionRule, int color, byte friendlyFire, String[] players) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nameTagVisibility = nameTagVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.friendlyFire = friendlyFire;
        this.players = players;
    }

    public PacketPlayServerScoreboardTeam() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_TEAM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getNameTagVisibility() {
        return nameTagVisibility;
    }

    public void setNameTagVisibility(String nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public String getCollisionRule() {
        return collisionRule;
    }

    public void setCollisionRule(String collisionRule) {
        this.collisionRule = collisionRule;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public byte getFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(byte friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.name = protoBuf.readString();
        this.mode = protoBuf.readByte();

        if (this.mode == 0 || this.mode == 2) {
            this.displayName = protoBuf.readString();
            this.prefix = protoBuf.readString();
            this.suffix = protoBuf.readString();
            this.friendlyFire = protoBuf.readByte();
            this.nameTagVisibility = protoBuf.readString();
            this.color = protoBuf.readByte();
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            int length = protoBuf.readVarInt();
            this.players = new String[length];

            for (int i = 0; i < length; i++) {
                this.players[i] = protoBuf.readString();
            }
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.name);
        protoBuf.writeByte(this.mode);

        if (this.mode == 0 || this.mode == 2) {
            protoBuf.writeString(this.displayName);
            protoBuf.writeString(this.prefix);
            protoBuf.writeString(this.suffix);
            protoBuf.writeByte(this.friendlyFire);
            protoBuf.writeString(this.nameTagVisibility);
            protoBuf.writeByte(this.color);
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            protoBuf.writeVarInt(this.players.length);
            for (String player : this.players) {
                protoBuf.writeString(player);
            }
        }
    }

    public String toString() {
        return "PacketPlayServerScoreboardTeam(name=" + this.getName()
                + ", mode=" + this.getMode()
                + ", displayName=" + this.getDisplayName()
                + ", prefix=" + this.getPrefix()
                + ", suffix=" + this.getSuffix()
                + ", nameTagVisibility=" + this.getNameTagVisibility()
                + ", collisionRule=" + this.getCollisionRule()
                + ", color=" + this.getColor()
                + ", friendlyFire=" + this.getFriendlyFire()
                + ", players=" + Arrays.deepToString(this.getPlayers())
                + ")";
    }
}
