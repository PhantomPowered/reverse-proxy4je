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
package com.github.derrop.proxy.plugins.gomme.secret;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamEntryRemoveEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamRegisterEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUnregisterEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUpdateEvent;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.spectator.SpectatorJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.spectator.SpectatorLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.parse.MatchParser;

import java.util.Arrays;
import java.util.Map;

public class GommeSpectatorDetector extends MatchParser {

    public GommeSpectatorDetector(GommeStatsCore core) {
        super(core);
    }

    @Listener
    public void handleScoreboardTeamUpdate(ScoreboardTeamUpdateEvent event) {
        this.handleTeamAddEntry(event.getConnection(), event.getTeam());
    }

    @Listener
    public void handleScoreboardTeamCreate(ScoreboardTeamRegisterEvent event) {
        this.handleTeamAddEntry(event.getConnection(), event.getTeam());
    }

    private void handleTeamAddEntry(ServiceConnection connection, Team team) {
        if (team.getEntries().isEmpty()) {
            return;
        }

        MatchInfo matchInfo = this.getMatch(connection, team);
        if (matchInfo == null) {
            return;
        }

        for (String name : team.getEntries()) {
            if (matchInfo.getProperty("spectator-" + name) != null) {
                continue;
            }

            if (connection.getPlayer() != null) {
                connection.getPlayer().sendMessage("§eGomme-Spectator §8| §7Spectator joined: §e" + name);
            }

            System.out.println("Spectator joined: " + name);
            matchInfo.setProperty("spectator-" + name, new SpectatorEntry(name, team.getName()));
            matchInfo.callEvent(new SpectatorJoinEvent(name));
        }
    }

    @Listener
    public void handleTeamDelete(ScoreboardTeamUnregisterEvent event) {
        Team team = event.getTeam();
        MatchInfo matchInfo = this.getMatch(event.getConnection(), team);
        if (matchInfo == null) {
            return;
        }

        for (String entry : team.getEntries()) {
            this.removeSpectator(event.getConnection(), matchInfo, entry);
        }
    }

    // pretty weird, but if a spectator leaves, this will be called once and if a spectator joins, this will be called multiple times (only tested in cores and bedwars)
    @Listener
    public void handleScoreboardTeamEntryRemove(ScoreboardTeamEntryRemoveEvent event) {
        Team team = event.getTeam();
        MatchInfo matchInfo = this.getMatch(event.getConnection(), team);
        if (matchInfo == null) {
            return;
        }

        this.removeSpectator(event.getConnection(), matchInfo, event.getEntry());

        if (Arrays.stream(event.getConnection().getWorldDataProvider().getOnlinePlayers()).anyMatch(playerInfo -> playerInfo.getUsername().equals(event.getEntry()))) {
            // in bedwars however, this is called even for players who are playing in game
            return;
        }

        for (Map.Entry<String, Object> entry : matchInfo.getProperties().entrySet()) {
            SpectatorEntry spec = (SpectatorEntry) entry.getValue();
            if (spec.getPlayer().equals(event.getEntry()) && spec.getTeam().equals(event.getTeam().getName())) {
                if (spec.isLeft()) {
                    String name = spec.getPlayer();

                    // TODO this doesn't work in BedWars yet
                    System.out.println("Spectator left: " + name);


                    matchInfo.callEvent(new SpectatorLeaveEvent(name));
                } else {
                    spec.setLastRemove();
                }
            }
        }

    }

    private void removeSpectator(ServiceConnection connection, MatchInfo matchInfo, String spectator) {
        if (!matchInfo.getProperties().containsKey("spectator-" + spectator)) {
            return;
        }

        SpectatorEntry entry = matchInfo.getProperty("spectator-" + spectator);
        if (entry == null) {
            return;
        }
        matchInfo.getProperties().remove("spectator-" + spectator);


        if (connection.getPlayer() != null) {
            connection.getPlayer().sendMessage("§eGomme-Spectator §8| §7Spectator left: §e" + spectator);
        }
    }

    private MatchInfo getMatch(ServiceConnection connection, Team team) {
        if (!team.getName().startsWith("ZZZ")) { // spectator teams start with ZZZ
            return null;
        }
        MatchInfo matchInfo = super.getMatchInfo(connection);
        if (matchInfo == null || !matchInfo.isRunning()) {
            return null;
        }
        return matchInfo;
    }

}
