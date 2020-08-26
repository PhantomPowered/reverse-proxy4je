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
import com.github.phantompowered.proxy.api.player.GameMode;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class BasicPlayerInfo implements PlayerInfo {

    private final GameProfile profile;

    private final GameMode gamemode;

    private final int ping;

    private final String displayName;

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
}
