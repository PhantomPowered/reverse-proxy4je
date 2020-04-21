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
package com.github.derrop.proxy.basic;

import com.github.derrop.proxy.api.connection.ServiceWorldDataProvider;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;
import com.github.derrop.proxy.api.entity.player.GameMode;
import com.github.derrop.proxy.connection.cache.handler.GameStateCache;
import com.github.derrop.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.derrop.proxy.connection.cache.handler.SimplePacketCache;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerTimeUpdate;

import java.util.UUID;
import java.util.function.Function;

public class BasicServiceWorldDataProvider implements ServiceWorldDataProvider {

    private BasicServiceConnection connection;

    public BasicServiceWorldDataProvider(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private PacketPlayServerTimeUpdate getLastTimeUpdate() {
        return (PacketPlayServerTimeUpdate) ((SimplePacketCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler.getPacketIDs()[0] == ProtocolIds.ToClient.Play.UPDATE_TIME)).getLastPacket();
    }

    private GameStateCache getGameStateCache() {
        return (GameStateCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof GameStateCache);
    }

    @Override
    public long getWorldTime() {
        PacketPlayServerTimeUpdate update = this.getLastTimeUpdate();
        return update == null ? -1 : update.getWorldTime();
    }

    @Override
    public long getTotalWorldTime() {
        PacketPlayServerTimeUpdate update = this.getLastTimeUpdate();
        return update == null ? -1 : update.getTotalWorldTime();
    }

    @Override
    public boolean isRaining() {
        return this.getGameStateCache().isRaining();
    }

    @Override
    public boolean isThundering() {
        return this.getGameStateCache().isThundering();
    }

    @Override
    public float getRainStrength() {
        return this.getGameStateCache().getRainStrength();
    }

    @Override
    public float getThunderStrength() {
        return this.getGameStateCache().getThunderStrength();
    }

    @Override
    public GameMode getOwnGameMode() {
        return this.getGameStateCache().getGameMode();
    }

    @Override
    public PlayerInfo[] getOnlinePlayers() {
        PlayerInfoCache cache = (PlayerInfoCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof PlayerInfoCache);

        return cache.getItems().stream()
                .map(this.itemToPlayerInfoFunction())
                .toArray(PlayerInfo[]::new);
    }

    @Override
    public PlayerInfo getOnlinePlayer(UUID uniqueId) {
        PlayerInfoCache cache = (PlayerInfoCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof PlayerInfoCache);

        return cache.getItems().stream()
                .filter(item -> item.getUniqueId().equals(uniqueId))
                .findFirst()
                .map(this.itemToPlayerInfoFunction())
                .orElse(null);
    }

    private Function<PacketPlayServerPlayerInfo.Item, PlayerInfo> itemToPlayerInfoFunction() {
        return item -> new BasicPlayerInfo(item.getUniqueId(), item.getUsername(), item.getProperties(), GameMode.getById(item.getGamemode()), item.getPing(), item.getDisplayName());
    }

}
