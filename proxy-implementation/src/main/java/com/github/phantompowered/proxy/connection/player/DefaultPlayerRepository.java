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

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.player.OfflinePlayer;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.PlayerRepository;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.storage.OfflinePlayerStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultPlayerRepository implements PlayerRepository {

    private final ServiceRegistry registry;
    private final OfflinePlayerStorage storage;

    public DefaultPlayerRepository(ServiceRegistry registry) {
        this.registry = registry;
        this.storage = new OfflinePlayerStorage(registry);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return this.registry.getProviderUnchecked(ServiceConnector.class)
                .getOnlineClients().stream().map(ServiceConnection::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Player getOnlinePlayer(String name) {
        return this.registry.getProviderUnchecked(ServiceConnector.class)
                .getOnlineClients().stream()
                .map(ServiceConnection::getPlayer)
                .filter(Objects::nonNull)
                .filter(connection -> connection.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    @Override
    public Player getOnlinePlayer(UUID uniqueId) {
        return this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                .map(ServiceConnection::getPlayer)
                .filter(Objects::nonNull)
                .filter(connection -> connection.getUniqueId().equals(uniqueId))
                .findFirst().orElse(null);
    }

    @Override
    public void insertOfflinePlayer(OfflinePlayer offlinePlayer) {
        this.storage.insertOfflinePlayer(this.asOfflinePlayer(offlinePlayer));
    }

    @Override
    public void updateOfflinePlayer(OfflinePlayer offlinePlayer) {
        Player onlinePlayer = this.getOnlinePlayer(offlinePlayer.getUniqueId());
        if (onlinePlayer instanceof DefaultPlayer) {
            ((DefaultPlayer) onlinePlayer).applyPermissions(offlinePlayer);
        }

        this.storage.updateOfflinePlayer(this.asOfflinePlayer(offlinePlayer));
    }

    private OfflinePlayer asOfflinePlayer(OfflinePlayer offlinePlayer) {
        if (offlinePlayer instanceof Player) {
            // create a new player because we don't want the online player to be stored in the database
            return new DefaultOfflinePlayer(
                    offlinePlayer.getUniqueId(), offlinePlayer.getName(), offlinePlayer.getLastLogin(), offlinePlayer.getLastVersion(), offlinePlayer.getEffectivePermissions()
            );
        }
        return offlinePlayer;
    }

    @Override
    public Collection<? extends OfflinePlayer> getOfflinePlayers() {
        return this.storage.getOfflinePlayers();
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        Player player = this.getOnlinePlayer(name);
        return player != null ? player : this.storage.getOfflinePlayer(name);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        Player player = this.getOnlinePlayer(uniqueId);
        return player != null ? player : this.storage.getOfflinePlayer(uniqueId);
    }
}
