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
package com.github.phantompowered.proxy.connection.player;

import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.GameMode;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BasicPlayerInfo implements PlayerInfo {

    private GameProfile profile;

    private GameMode gamemode;

    private int ping;

    private String displayName;

    public BasicPlayerInfo(GameProfile profile, GameMode gamemode, int ping, String displayName) {
        this.profile = profile;
        this.gamemode = gamemode;
        this.ping = ping;
        this.displayName = displayName;
    }

    @Override
    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public UUID getUniqueId() {
        return this.profile.getId();
    }

    @Override
    public String getUsername() {
        return this.profile.getName();
    }

    @Override
    public GameMode getGamemode() {
        return this.gamemode;
    }

    @Override
    public int getPing() {
        return this.ping;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return "BasicPlayerInfo{"
                + "profile=" + profile
                + ", gamemode=" + gamemode
                + ", ping=" + ping
                + ", displayName='" + displayName + '\''
                + '}';
    }

    @Override
    public void write(@NotNull ProtoBuf buf) {
        buf.writeUniqueId(this.profile.getId());
        buf.writeString(this.profile.getName());

        buf.writeVarInt(this.profile.getProperties().size());
        for (Property property : this.profile.getProperties().values()) {
            buf.writeString(property.getName());
            buf.writeString(property.getValue());
            buf.writeBoolean(property.hasSignature());
            if (property.hasSignature()) {
                buf.writeString(property.getSignature());
            }
        }

        buf.writeByte(this.gamemode.getId());
        buf.writeVarInt(this.ping);
        buf.writeBoolean(this.displayName != null);
        if (this.displayName != null) {
            buf.writeString(this.displayName);
        }
    }

    @Override
    public void read(@NotNull ProtoBuf buf) {
        this.profile = new GameProfile(buf.readUniqueId(), buf.readString());

        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            String name = buf.readString();
            String value = buf.readString();
            String signature = buf.readBoolean() ? buf.readString() : null;
            this.profile.getProperties().put(name, signature != null ? new Property(name, value, signature) : new Property(name, value));
        }

        this.gamemode = GameMode.getById(buf.readByte());
        this.ping = buf.readVarInt();
        this.displayName = buf.readBoolean() ? buf.readString() : null;
    }
}
