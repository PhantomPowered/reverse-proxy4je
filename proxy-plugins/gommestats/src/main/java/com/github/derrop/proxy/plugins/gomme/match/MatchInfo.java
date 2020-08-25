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

import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.player.id.PlayerId;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.events.GommeMatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchBeginEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndFinishedEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MatchInfo {

    public static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(MatchEvent.class, new MatchEventSerializer()).create();
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(MatchEvent.class, new MatchEventSerializer()).create();

    private final transient MatchManager matchManager;
    private final transient ServiceConnection invoker;

    private final PlayerId recorderId;
    private final GommeServerType gameMode;
    private final String matchId;
    private transient boolean running;
    private long beginTimestamp = -1;
    private long endTimestamp = -1;

    private final Collection<PlayerInfo> players = new ArrayList<>();
    private final Collection<UUID> bacPlayers = new ArrayList<>(); // TODO fill

    private final Collection<MatchTeam> teams = new ArrayList<>();
    private final Collection<MatchEvent> events = new ArrayList<>();

    private final transient Map<String, Object> properties = new ConcurrentHashMap<>();

    private Language selectedLanguage;

    public MatchInfo(MatchManager matchManager, ServiceConnection invoker, GommeServerType gameMode, String matchId) {
        this.matchManager = matchManager;
        this.invoker = invoker;
        this.recorderId = new PlayerId(invoker.getUniqueId(), invoker.getName());
        this.gameMode = gameMode;
        this.matchId = matchId;

        // TODO delay:
        this.players.addAll(Arrays.asList(invoker.getWorldDataProvider().getOnlinePlayers()));

        this.selectedLanguage = invoker.getProperty(GommeConstants.SELECTED_LANGUAGE);
        if (this.selectedLanguage == null) {
            this.selectedLanguage = Language.GERMANY;
        }
    }

    protected void start() {
        this.running = true;
        this.beginTimestamp = System.currentTimeMillis();
        APIUtil.SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            // TODO this shouldn't be done in this method but in the scoreboard update method
            for (PlayerInfo player : this.players) {
                Team scoreTeam = this.invoker.getScoreboard().getTeamByEntry(player.getDisplayName() != null ? player.getDisplayName() : player.getUsername());
                if (scoreTeam == null) {
                    continue;
                }

                String prefix = ChatColor.stripColor(scoreTeam.getPrefix()).split(" ")[0];
                MessageType type = this.matchManager.getTeamRegistry().getTeam(this.selectedLanguage, this.gameMode, prefix);
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

    protected void end() {
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
        if (event instanceof MatchBeginEvent) {
            this.matchManager.startMatch(this);
        } else if (event instanceof MatchEndFinishedEvent) {
            this.matchManager.endMatch(this);
        }

        this.events.add(event);
        this.invoker.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new GommeMatchEvent(this, event));
    }

    public Language getSelectedLanguage() {
        return this.selectedLanguage;
    }

    public void setSelectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public ServiceConnection getInvoker() {
        return this.invoker;
    }

    public GommeServerType getGameMode() {
        return this.gameMode;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getBeginTimestamp() {
        return this.beginTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    public long getLengthInMillis() {
        return this.beginTimestamp != -1 && this.endTimestamp != -1 ? this.endTimestamp - this.beginTimestamp : -1;
    }

    public Collection<PlayerInfo> getPlayers() {
        return this.players;
    }

    public Collection<UUID> getBacPlayers() {
        return this.bacPlayers;
    }

    public Collection<MatchEvent> getEvents() {
        return this.events;
    }

    public Collection<MatchTeam> getTeams() {
        return this.teams;
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

    public String toReadableText() {
        StringBuilder builder = new StringBuilder();
        builder.append("=========================== General Information ===========================\n");
        builder.append("GameMode: ").append(this.gameMode.getDisplayName()).append('\n');
        builder.append("Recorded by: ").append(this.recorderId.toString()).append('\n');
        builder.append("MatchId: ").append(this.matchId).append('\n');
        builder.append("Begin: ").append(this.beginTimestamp == -1 ? "Not recorded" : FORMAT.format(this.beginTimestamp)).append('\n');
        builder.append("End: ").append(this.endTimestamp == -1 ? "Not recorded" : FORMAT.format(this.endTimestamp)).append('\n');
        long length = this.getLengthInMillis();
        builder.append("Length: ").append(length == -1 ? "Not recorded" : (length / 1000 / 60) + " minutes").append('\n');
        builder.append("=========================== General Information ===========================\n");

        builder.append("\n\n");

        builder.append("=========================== Players ===========================\n");
        for (PlayerInfo player : this.players) {
            MatchTeam team = this.teams.stream().filter(filter -> filter.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
            boolean hasDisplayName = player.getDisplayName() != null;
            builder.append("- ").append(player.getUniqueId()).append('#').append(player.getUsername()).append(" (Display: ");
            if (hasDisplayName) {
                builder.append('\'').append(player.getDisplayName()).append('\'');
            } else {
                builder.append("none");
            }
            builder.append(") Team: ").append(team == null ? "no team" : team.getType());
            if (this.bacPlayers.contains(player.getUniqueId())) {
                builder.append(" | BAC");
            }
            builder.append('\n');
        }
        builder.append("=========================== Players ===========================\n");

        builder.append("\n\n");

        builder.append("=========================== Events (").append(this.events.size()).append(") ===========================\n");
        for (MatchEvent event : this.events) {
            builder.append(FORMAT.format(event.getTimestamp())).append(": ");

            if (event.isHighlighted()) {
                builder.append("========================================= ");
            }

            builder.append(event.toPlainText());

            if (event.isHighlighted()) {
                builder.append(" =========================================");
            }

            builder.append('\n');
        }
        builder.append("=========================== Events (").append(this.events.size()).append(") ===========================\n");

        builder.append("\n\n");

        builder.append("=========================== JSON ===========================\n");
        builder.append(PRETTY_GSON.toJson(this)).append('\n');
        builder.append("=========================== JSON ===========================\n");

        return builder.toString();
    }

    @Override
    public String toString() {
        return "MatchInfo{" +
                "matchManager=" + matchManager +
                ", invoker=" + invoker +
                ", recorderId=" + recorderId +
                ", gameMode=" + gameMode +
                ", matchId='" + matchId + '\'' +
                ", running=" + running +
                ", beginTimestamp=" + beginTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", players=" + players +
                ", bacPlayers=" + bacPlayers +
                ", teams=" + teams +
                ", properties=" + properties +
                ", selectedLanguage=" + selectedLanguage +
                '}';
    }
}
