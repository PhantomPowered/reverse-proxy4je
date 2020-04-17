package com.github.derrop.proxy.api.events.connection.service.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.scoreboard.Score;
import org.jetbrains.annotations.NotNull;

public class ScoreboardScoreRemoveEvent extends ScoreboardEvent {

    private final Score score;

    public ScoreboardScoreRemoveEvent(@NotNull ServiceConnection connection, Score score) {
        super(connection);
        this.score = score;
    }

    public Score getScore() {
        return this.score;
    }
}
