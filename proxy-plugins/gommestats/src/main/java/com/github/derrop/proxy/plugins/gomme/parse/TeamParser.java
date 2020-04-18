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
package com.github.derrop.proxy.plugins.gomme.parse;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardScoreSetEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamRegisterEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUnregisterEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUpdateEvent;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchTeam;

import java.util.function.BiConsumer;

public class TeamParser extends MatchParser {

    public TeamParser(GommeStatsCore core) {
        super(core);
    }

    @Listener
    public void handleTeamRegister(ScoreboardTeamRegisterEvent event) {
        System.out.println("TCreate: " + event.getTeam().getName() + ": " + ChatColor.stripColor(event.getTeam().getPrefix()) + " " + event.getTeam().getEntries());
        this.getTeamColor(event.getConnection(), event.getTeam(), (matchInfo, color) -> {
            System.out.println(matchInfo.getMatchId() + ": " + color);
        });
    }

    @Listener
    public void handleTeamUpdate(ScoreboardTeamUpdateEvent event) {
        System.out.println("TUpdate: " + event.getTeam().getName() + ": " + ChatColor.stripColor(event.getTeam().getPrefix()) + " " + event.getTeam().getEntries());
    }

    @Listener
    public void hadnleTeamUnregister(ScoreboardTeamUnregisterEvent event) {

    }

    @Listener
    public void handleScoreUpdate(ScoreboardScoreSetEvent event) {
        System.out.println("Score: " + ChatColor.stripColor(event.getScore().getEntry()) + ": " + event.getScore().getScore());
    }

    private void getTeamColor(ServiceConnection connection, Team team, BiConsumer<MatchInfo, ChatColor> consumer) {
        MatchInfo matchInfo = super.getMatchInfo(connection);
        if (matchInfo == null || matchInfo.isRunning()) {
            return;
        }

        ChatColor color = ChatColor.getLastColor(team.getPrefix());
        if (color == null) {
            System.err.println("Team with color " + team.getPrefix() + " not found! (Name: " + team.getName() + ")");
            return;
        }

        consumer.accept(matchInfo, color);
    }

    private void getTeam(ServiceConnection connection, Team team, BiConsumer<MatchInfo, MatchTeam> consumer) {
        this.getTeamColor(connection, team, (matchInfo, color) ->
                matchInfo.getTeams().stream().filter(matchTeam -> matchTeam.getColor().equals(color)).findFirst()
                        .ifPresent(matchTeam -> consumer.accept(matchInfo, matchTeam)));
    }
    
}
