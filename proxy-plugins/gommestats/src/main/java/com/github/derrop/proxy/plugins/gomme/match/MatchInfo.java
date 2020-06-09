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

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageRegistry;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.player.PlayerData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MatchInfo {

    private final transient ServiceConnection invoker;
    private final GommeGameMode gameMode;
    private final String matchId;
    private transient boolean running;
    private long beginTimestamp = -1;
    private long endTimestamp = -1;
    private final Collection<PlayerInfo> players = new ArrayList<>();

    private final Collection<MatchTeam> teams = new ArrayList<>();
    private final Collection<MatchEvent> events = new ArrayList<>();

    private final transient Map<String, Object> properties = new ConcurrentHashMap<>();

    public MatchInfo(ServiceConnection invoker, GommeGameMode gameMode, String matchId) {
        this.invoker = invoker;
        this.gameMode = gameMode;
        this.matchId = matchId;

        this.players.addAll(Arrays.asList(invoker.getWorldDataProvider().getOnlinePlayers()));
    }

    public void start() {
        this.running = true;
        this.beginTimestamp = System.currentTimeMillis();
        Constants.SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            for (PlayerInfo player : this.players) {
                Team scoreTeam = this.invoker.getScoreboard().getTeamByEntry(player.getDisplayName() != null ? player.getDisplayName() : player.getUsername());
                if (scoreTeam == null) {
                    continue;
                }

                String prefix = ChatColor.stripColor(scoreTeam.getPrefix()).split(" ")[0];
                // TODO language should be dynamic
                MessageType type = MessageRegistry.getTeam(Language.GERMAN, this.gameMode, prefix);
                if (type == null) {
                    continue;
                }

                MatchTeam team = this.teams.stream()
                        .filter(matchTeam -> matchTeam.getType().equals(type))
                        .findFirst().orElseGet(() -> {
                            MatchTeam matchTeam = new MatchTeam(type, new HashSet<>());
                            this.teams.add(matchTeam);
                            return matchTeam;
                        });

                team.getPlayers().add(player.getUniqueId());
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    public void end() {
        this.running = false;
        this.endTimestamp = System.currentTimeMillis();
    }

    public boolean hasBegin() {
        return this.beginTimestamp > 0;
    }

    public boolean hasEnded() {
        return this.endTimestamp > 0;
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

    public Collection<PlayerInfo> getPlayers() {
        return this.players;
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
