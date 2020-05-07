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

import com.github.derrop.proxy.api.connection.player.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultOfflinePlayer implements OfflinePlayer, Serializable {

    private static final long serialVersionUID = 3018504430617681860L;

    public DefaultOfflinePlayer(UUID uniqueID, String name, long lastLogin, int lastVersion) {
        this(uniqueID, name, lastLogin, lastVersion, new ConcurrentHashMap<>());
    }

    public DefaultOfflinePlayer(UUID uniqueID, String name, long lastLogin, int lastVersion, Map<String, Boolean> permissions) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.lastLogin = lastLogin;
        this.lastVersion = lastVersion;
        this.permissions = permissions;
    }

    private final UUID uniqueID;
    private final String name;
    private final long lastLogin;
    private final int lastVersion;

    private Map<String, Boolean> permissions;

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

    @Override
    public boolean hasPermission(@NotNull String permission) {
        if (this.permissions.containsKey(permission)) {
            return this.permissions.get(permission);
        }
        return this.permissions.containsKey("*") && this.permissions.get("*");
    }

    @Override
    public void addPermission(@NotNull String permission, boolean set) {
        if (this.permissions.containsKey(permission)) {
            return;
        }

        this.permissions.put(permission, set);
    }

    @Override
    public void removePermission(@NotNull String permission) {
        this.permissions.remove(permission);
    }

    @Override
    public void clearPermissions() {
        this.permissions.clear();
    }

    @Override
    public @NotNull Map<String, Boolean> getEffectivePermissions() {
        return this.permissions;
    }

}
