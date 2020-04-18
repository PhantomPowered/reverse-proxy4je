package com.github.derrop.proxy.plugins.gomme.secret;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamEntryRemoveEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUnregisterEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUpdateEvent;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.event.SpectatorJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.SpectatorLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.parse.MatchParser;

import java.util.Arrays;
import java.util.Map;

public class GommeSpectatorDetector extends MatchParser {

    public GommeSpectatorDetector(GommeStatsCore core) {
        super(core);
    }

    // yes, it is possible to detect the spectators in a round while playing, they will be in a team and those teams are always sent to every player on the server
    // the team name of spectators always starts with "ZZZ"

    @Listener
    public void handleScoreboardTeamUpdate(ScoreboardTeamUpdateEvent event) {
        MatchInfo matchInfo = this.getMatch(event.getConnection(), event.getTeam());
        if (matchInfo == null) {
            return;
        }

        for (String name : event.getTeam().getEntries()) {
            if (matchInfo.getProperty("spectator-" + name) != null) {
                continue;
            }

            System.out.println("Spectator joined: " + name);
            matchInfo.setProperty("spectator-" + name, new SpectatorEntry(name, event.getTeam().getName()));
            matchInfo.callEvent(new SpectatorJoinEvent(name));
        }
    }

    // pretty weird, but if a spectator leaves, this will be called once and if a spectator joins, this will be called multiple times (only tested in cores and bedwars)
    @Listener
    public void handleScoreboardTeamEntryRemove(ScoreboardTeamEntryRemoveEvent event) {
        MatchInfo matchInfo = this.getMatch(event.getConnection(), event.getTeam());
        if (matchInfo == null) {
            return;
        }

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
                    matchInfo.getProperties().remove("spectator-" + name);
                    matchInfo.callEvent(new SpectatorLeaveEvent(name));
                } else {
                    spec.setLastRemove();
                }
            }
        }

    }

    private MatchInfo getMatch(ServiceConnection connection, Team team) {
        if (!team.getName().startsWith("ZZZ")) { // spectator teams start with ZZZ
            return null;
        }
        MatchInfo matchInfo = super.getMatchInfo(connection);
        /*if (!matchInfo.isRunning()) { TODO the running method is not implemented yet
            return;
        }*/
        return matchInfo;
    }

}
