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
package com.github.derrop.proxy.plugins.gomme.match;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.player.PlayerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MatchManager extends DatabaseProvidedStorage<JsonObject> {

    private final GommeStatsCore core;
    private final Gson gson;

    public MatchManager(GommeStatsCore core) {
        super(core.getRegistry(), "gomme_matches", JsonObject.class);
        this.core = core;
        this.gson = new GsonBuilder().setPrettyPrinting() /* TODO remove */.registerTypeAdapter(MatchEvent.class, new MatchEventSerializer()).create();
    }

    private final Map<String, MatchInfo> openMatches = new ConcurrentHashMap<>();

    public GommeStatsCore getCore() {
        return this.core;
    }

    public Collection<MatchInfo> getRunningMatches() {
        return this.openMatches.values().stream().filter(MatchInfo::isRunning).collect(Collectors.toList());
    }

    public Collection<MatchInfo> getRunningMatches(GommeGameMode gameMode) {
        return this.getRunningMatches().stream().filter(matchInfo -> matchInfo.getGameMode() == gameMode).collect(Collectors.toList());
    }

    public MatchInfo getMatch(String matchId) {
        return this.openMatches.get(matchId);
    }

    public MatchInfo getMatch(ServiceConnection connection) {
        for (MatchInfo value : this.openMatches.values()) {
            if (value.getInvoker().equals(connection) || Arrays.stream(value.getInvoker().getWorldDataProvider().getOnlinePlayers())
                    .anyMatch(playerInfo -> playerInfo.getUniqueId().equals(connection.getUniqueId()))) {
                return value;
            }
        }
        return null;
    }

    public void createMatch(MatchInfo matchInfo) {
        this.openMatches.put(matchInfo.getMatchId(), matchInfo);
    }

    public void deleteMatch(ServiceConnection invoker, MatchEvent event) {
        for (MatchInfo value : this.openMatches.values()) {
            if (value.getInvoker().equals(invoker)) {
                value.callEvent(event);
                this.openMatches.remove(value.getMatchId());
                this.writeToDatabase(value);
            }
        }
    }

    public void startMatch(MatchInfo matchInfo) {
        if (matchInfo.isRunning()) {
            return;
        }
        matchInfo.start();
    }

    public void endMatch(MatchInfo matchInfo) {
        if (matchInfo.hasEnded()) {
            return;
        }
        matchInfo.end();
    }

    private void writeToDatabase(MatchInfo matchInfo) {
        for (MatchEvent event : matchInfo.getEvents()) {
            System.out.println(new SimpleDateFormat("hh:mm:ss").format(event.getTimestamp()) + ": " + event.toPlainText());
        }
        System.out.println(this.gson.toJson(matchInfo));
        super.insert(matchInfo.getMatchId(), this.gson.toJsonTree(matchInfo).getAsJsonObject());
    }

    public long countMatches() {
        return super.size();
    }

    public long countMatches(GommeGameMode gameMode) {
        return super.getAll().stream()
                .map(jsonObject -> this.gson.fromJson(jsonObject, MatchInfo.class))
                .filter(matchInfo -> matchInfo.getGameMode() == gameMode)
                .count();
    }

    public Collection<MatchInfo> getPastMatches() {
        return super.getAll().stream().map(jsonObject -> this.gson.fromJson(jsonObject, MatchInfo.class)).collect(Collectors.toList());
    }

}
