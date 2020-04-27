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
package com.github.derrop.proxy.storage;

import com.github.derrop.proxy.api.entity.player.OfflinePlayer;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.entity.player.DefaultOfflinePlayer;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.UUID;

public class OfflinePlayerStorage extends DatabaseProvidedStorage<DefaultOfflinePlayer> {

    private UUIDNameStorage uuidNameStorage;

    public OfflinePlayerStorage(ServiceRegistry registry) {
        super(registry, "player_storage", DefaultOfflinePlayer.class);
        this.uuidNameStorage = new UUIDNameStorage(registry, "name_uuid_map");
    }

    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        return super.get(uniqueId.toString());
    }

    public void insertOfflinePlayer(OfflinePlayer player) {
        super.insert(player.getUniqueId().toString(), (DefaultOfflinePlayer) player);
    }

    public void updateOfflinePlayer(OfflinePlayer player) {
        OfflinePlayer oldPlayer = super.get(player.getUniqueId().toString());
        if (oldPlayer != null) {
            this.uuidNameStorage.remove(oldPlayer.getName());
        }

        this.uuidNameStorage.put(player.getName(), player.getUniqueId());

        super.update(player.getUniqueId().toString(), (DefaultOfflinePlayer) player);
    }

    public Collection<? extends OfflinePlayer> getOfflinePlayers() {
        return super.getAll();
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        UUID uniqueId = this.uuidNameStorage.getUUID(name);
        return uniqueId != null ? this.getOfflinePlayer(uniqueId) : null;
    }

}
