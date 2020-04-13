package com.github.derrop.proxy.scoreboard;

import com.github.derrop.proxy.api.scoreboard.DisplaySlot;
import com.github.derrop.proxy.api.scoreboard.Objective;
import com.github.derrop.proxy.api.scoreboard.Score;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.scoreboard.minecraft.ScoreObjective;
import com.github.derrop.proxy.scoreboard.minecraft.criteria.IScoreObjectiveCriteria;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

@ToString
public class BasicObjective implements Objective {

    private final BasicScoreboard scoreboard;
    private String name;
    private ScoreObjective handle;

    public BasicObjective(BasicScoreboard scoreboard, String name, ScoreObjective handle) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.handle = handle;
    }

    public void ensureRegistered() { // maybe received unregister by server?
        if (this.scoreboard.getHandle().getObjective(this.name) == null || this.handle == null ||
                this.scoreboard.getHandle().getObjectiveInDisplaySlot(this.handle.getDisplaySlot()) != this.handle) {

            if (this.handle != null && this.scoreboard.getHandle().getObjectiveInDisplaySlot(this.handle.getDisplaySlot()) != null) {
                Collection<com.github.derrop.proxy.scoreboard.minecraft.Score> scores = this.scoreboard.getHandle().getSortedScores(this.handle);

                this.handle = this.scoreboard.getHandle().getObjectiveInDisplaySlot(this.handle.getDisplaySlot());
                this.name = this.handle.getName();

                for (com.github.derrop.proxy.scoreboard.minecraft.Score score : scores) {
                    this.scoreboard.getHandle().getValueFromObjective(score.getPlayerName(), this.handle).setScorePoints(score.getScorePoints());
                    this.scoreboard.getCache().sendScoreUpdate(score.getPlayerName(), this.name, score.getScorePoints());
                }

            } else {
                ScoreObjective newObjective = this.scoreboard.getHandle().addScoreObjective(this.name, IScoreObjectiveCriteria.DUMMY);

                this.scoreboard.getHandle().setObjectiveInDisplaySlot(this.handle != null ? this.handle.getDisplaySlot() : DisplaySlot.SIDEBAR.ordinal(), newObjective);
                this.handle = newObjective;

            }
            this.scoreboard.getCache().sendCreatedObjective(this.handle);
        }
    }

    @NotNull
    public ScoreObjective getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public @NotNull String getCriteria() {
        this.ensureRegistered();
        return this.handle.getCriteria().getName();
    }

    @Override
    public @NotNull String getName() {
        this.ensureRegistered();
        return this.handle.getName();
    }

    @Override
    public @NotNull String getDisplayName() {
        this.ensureRegistered();
        return this.handle.getDisplayName();
    }

    @Override
    public @Nullable DisplaySlot getDisplaySlot() {
        this.ensureRegistered();
        return this.handle.getDisplaySlot() < 0 ? null : DisplaySlot.values()[this.handle.getDisplaySlot()];
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        this.ensureRegistered();
        this.handle.setDisplayName(displayName);
    }

    @Override
    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        this.ensureRegistered();
        this.handle.setDisplaySlot(displaySlot.ordinal());
    }

    @Override
    public void unregister() {
        this.scoreboard.removeObjective(this);
        this.scoreboard.getCache().getScoreboard().setObjectiveInDisplaySlot(this.handle.getDisplaySlot(), null);
        this.scoreboard.getCache().sendDeletedObjective(this.name);
    }

    @Override
    public @NotNull Score getScore(@NotNull String entry) {
        this.ensureRegistered();
        int value = this.scoreboard.getHandle().getValueFromObjective(entry, this.handle).getScorePoints();
        return new BasicScore(this.scoreboard, this, entry, value);
    }

    @Override
    public void unregisterScore(@NotNull String entry) {
        this.scoreboard.getHandle().removeObjectiveFromEntity(entry, this.handle);
        this.scoreboard.getCache().sendScoreDestroy(entry, this.name);
    }
}
