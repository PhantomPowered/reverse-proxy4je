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
package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.player.clan.ClanInfo;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerDataProvider extends DatabaseProvidedStorage<PlayerData> {

    public PlayerDataProvider(ServiceRegistry registry) {
        super(registry, "gomme_player_data", PlayerData.class);
    }

    public PlayerData getData(UUID uniqueId) {
        return super.get(uniqueId.toString());
    }

    public void updateStatistics(PlayerData data) {
        super.insertOrUpdate(data.getPlayerInfo().getUniqueId().toString(), data);
    }

    public long countPlayerData() {
        return super.size();
    }

    public long countPlayerData(GommeGameMode gameMode) {
        return super.getAll().stream().filter(playerData -> playerData.getStatistics().containsKey(gameMode)).count();
    }

    public PlayerData getBestPlayer(GommeGameMode gameMode) {
        return this.sortedPlayerDataStream(gameMode).min(Collections.reverseOrder()).orElse(null);
    }

    public PlayerData getWorstPlayer(GommeGameMode gameMode) {
        return this.sortedPlayerDataStream(gameMode).findFirst().orElse(null);
    }

    public Stream<PlayerData> sortedPlayerDataStream(GommeGameMode gameMode) {
        return super.getAll().stream()
                .filter(playerData -> playerData.getStatistics().containsKey(gameMode))
                .sorted(Comparator.comparing(value -> value.getStatistics().get(gameMode)));
    }

}
