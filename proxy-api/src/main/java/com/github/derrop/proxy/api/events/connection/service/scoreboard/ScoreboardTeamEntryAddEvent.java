package com.github.derrop.proxy.api.events.connection.service.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class ScoreboardTeamEntryAddEvent extends ScoreboardEvent {

    private final Team team;
    private final String entry;

    public ScoreboardTeamEntryAddEvent(@NotNull ServiceConnection connection, Team team, String entry) {
        super(connection);
        this.team = team;
        this.entry = entry;
    }

    public Team getTeam() {
        return this.team;
    }

    public String getEntry() {
        return this.entry;
    }
}
