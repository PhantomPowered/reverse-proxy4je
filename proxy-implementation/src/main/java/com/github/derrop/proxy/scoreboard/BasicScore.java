package com.github.derrop.proxy.scoreboard;

import com.github.derrop.proxy.api.scoreboard.Objective;
import com.github.derrop.proxy.api.scoreboard.Score;
import org.jetbrains.annotations.NotNull;

public class BasicScore implements Score {

    private final BasicScoreboard scoreboard;
    private final BasicObjective objective;
    private final String entry;
    private int value;

    public BasicScore(BasicScoreboard scoreboard, BasicObjective objective, String entry, int value) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.entry = entry;
        this.value = value;
    }

    @Override
    public @NotNull String getEntry() {
        return this.entry;
    }

    @Override
    public @NotNull Objective getObjective() {
        return this.objective;
    }

    @Override
    public int getScore() {
        return this.value;
    }

    @Override
    public void setScore(int score) {
        this.value = score;
        this.scoreboard.getHandle().getValueFromObjective(this.entry, this.objective.getHandle()).setScorePoints(score);
        this.scoreboard.getCache().sendScoreUpdate(this.entry, this.objective.getName(), this.value);
    }
}
