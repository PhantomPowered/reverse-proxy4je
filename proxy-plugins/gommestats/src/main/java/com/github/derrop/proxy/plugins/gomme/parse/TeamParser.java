package com.github.derrop.proxy.plugins.gomme.parse;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.handler.Listener;
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
