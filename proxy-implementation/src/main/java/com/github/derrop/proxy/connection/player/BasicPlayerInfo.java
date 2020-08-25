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
package com.github.derrop.proxy.connection.player;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.player.GameMode;

import java.util.Arrays;
import java.util.UUID;

public class BasicPlayerInfo implements PlayerInfo {

    private UUID uniqueId;

    private String username;
    private String[][] properties;

    private GameMode gamemode;

    private int ping;

    private String displayName;

    public BasicPlayerInfo(UUID uniqueId, String username, String[][] properties, GameMode gamemode, int ping, String displayName) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.properties = properties;
        this.gamemode = gamemode;
        this.ping = ping;
        this.displayName = displayName;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String[][] getProperties() {
        return this.properties;
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

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProperties(String[][] properties) {
        this.properties = properties;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "BasicPlayerInfo{"
                + "uniqueId=" + uniqueId
                + ", username='" + username + '\''
                + ", properties=" + Arrays.toString(properties)
                + ", gamemode=" + gamemode
                + ", ping=" + ping
                + ", displayName='" + displayName + '\''
                +
                '}';
    }
}
