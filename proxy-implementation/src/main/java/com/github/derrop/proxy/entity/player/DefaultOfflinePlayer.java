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
package com.github.derrop.proxy.entity.player;

import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import com.github.derrop.proxy.entity.permission.DefaultPermissionHolder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class DefaultOfflinePlayer extends DefaultPermissionHolder implements OfflinePlayer, Serializable {

    public DefaultOfflinePlayer(UUID uniqueID, String name, long lastLogin, int lastVersion) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.lastLogin = lastLogin;
        this.lastVersion = lastVersion;
    }

    private final UUID uniqueID;
    private final String name;
    private final long lastLogin;
    private final int lastVersion;

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueID;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public long getLastLogin() {
        return this.lastLogin;
    }

    @Override
    public int getLastVersion() {
        return this.lastVersion;
    }
}
