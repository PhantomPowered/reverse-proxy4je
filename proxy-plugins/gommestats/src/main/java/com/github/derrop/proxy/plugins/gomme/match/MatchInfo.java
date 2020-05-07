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
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.player.PlayerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchInfo {

    private final transient ServiceConnection invoker;
    private final GommeGameMode gameMode;
    private final String matchId;
    private transient boolean running;
    private long beginTimestamp;
    private long endTimestamp;
    private PlayerInfo[] playersOnBegin;
    private PlayerInfo[] playersOnEnd;

    private PlayerData[] statisticsOnBegin;

    private Collection<MatchTeam> teams = new ArrayList<>();
    private Collection<MatchEvent> events = new ArrayList<>();

    private transient Map<String, Object> properties = new ConcurrentHashMap<>();

    public MatchInfo(ServiceConnection invoker, GommeGameMode gameMode, String matchId) {
        this.invoker = invoker;
        this.gameMode = gameMode;
        this.matchId = matchId;
    }

    public void start(PlayerInfo[] currentPlayers, PlayerData[] statistics) {
        this.running = true;
        this.beginTimestamp = System.currentTimeMillis();
        this.playersOnBegin = currentPlayers;
        this.statisticsOnBegin = statistics;
    }

    public void end(PlayerInfo[] currentPlayers) {
        this.running = false;
        this.endTimestamp = System.currentTimeMillis();
        this.playersOnEnd = currentPlayers;
    }

    public boolean hasBegin() {
        return this.playersOnBegin != null;
    }

    public boolean hasEnded() {
        return this.playersOnEnd != null;
    }

    public void callEvent(MatchEvent event) {
        this.events.add(event);
    }

    public ServiceConnection getInvoker() {
        return invoker;
    }

    public GommeGameMode getGameMode() {
        return gameMode;
    }

    public String getMatchId() {
        return matchId;
    }

    public boolean isRunning() {
        return running;
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public PlayerInfo[] getPlayersOnBegin() {
        return playersOnBegin;
    }

    public PlayerInfo[] getPlayersOnEnd() {
        return playersOnEnd;
    }

    public PlayerData[] getStatisticsOnBegin() {
        return statisticsOnBegin;
    }

    public Collection<MatchEvent> getEvents() {
        return events;
    }

    public Collection<MatchTeam> getTeams() {
        return teams;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public <T> T getProperty(String key) {
        return (T) this.properties.get(key);
    }

    public <T> void setProperty(String key, T value) {
        this.properties.put(key, value);
    }

}
