package com.github.derrop.proxy.api.events.connection.service.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.events.connection.service.ServiceConnectionEvent;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class ScoreboardEvent extends ServiceConnectionEvent {
    public ScoreboardEvent(@NotNull ServiceConnection connection) {
        super(connection);
    }

    public Scoreboard getScoreboard() {
        return super.getConnection().getScoreboard();
    }

}
