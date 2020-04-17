package com.github.derrop.proxy.api.events.connection.service.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class ScoreboardTeamUpdateEvent extends ScoreboardEvent {

    private final Team team;

    public ScoreboardTeamUpdateEvent(@NotNull ServiceConnection connection, Team team) {
        super(connection);
        this.team = team;
    }

    public Team getTeam() {
        return this.team;
    }
}
