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
package com.github.derrop.proxy.connection.player.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.scoreboard.DisplaySlot;
import com.github.derrop.proxy.api.scoreboard.Objective;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.connection.cache.handler.scoreboard.ScoreboardCache;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.ScoreObjective;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BasicScoreboard implements Scoreboard {

    private final ServiceConnection connection;
    private final ScoreboardCache cache;

    private final Collection<BasicObjective> registeredObjectives = new CopyOnWriteArrayList<>();

    public BasicScoreboard(ServiceConnection connection, ScoreboardCache cache) {
        this.connection = connection;
        this.cache = cache;

        cache.setHandler(new BasicScoreboardHandler(this.connection, this));
    }

    public void removeObjective(BasicObjective objective) {
        this.registeredObjectives.remove(objective);
    }

    public Collection<BasicObjective> getRegisteredObjectives() {
        return this.registeredObjectives;
    }

    @NotNull
    public com.github.derrop.proxy.connection.player.scoreboard.minecraft.Scoreboard getHandle() {
        return this.cache.getScoreboard();
    }

    @NotNull
    public ScoreboardCache getCache() {
        return this.cache;
    }

    @Override
    public @NotNull ServiceConnection getAssociatedConnection() {
        return this.connection;
    }

    @Override
    public void clearSlot(@NotNull DisplaySlot displaySlot) {
        ScoreObjective objective = this.getHandle().getObjectiveInDisplaySlot(displaySlot.ordinal());
        if (objective == null) {
            return;
        }

        this.getHandle().setObjectiveInDisplaySlot(displaySlot.ordinal(), null);
        this.getCache().sendDeletedObjective(objective.getName());
    }

    @Override
    public @Nullable Objective getObjective(@NotNull String name) {
        ScoreObjective handle = this.getHandle().getObjective(name);
        return handle != null ? new BasicObjective(this, handle.getName(), handle) : null;
    }

    @Override
    public @Nullable Objective getObjective(@NotNull DisplaySlot displaySlot) {
        ScoreObjective handle = this.getHandle().getObjectiveInDisplaySlot(displaySlot.ordinal());
        return handle != null ? new BasicObjective(this, handle.getName(), handle) : null;
    }

    @Override
    public @NotNull Objective registerNewObjective(@NotNull String name) {
        if (this.getHandle().getObjective(name) != null) {
            throw new IllegalArgumentException("An objective with the name \"" + name + "\" is already registered");
        }

        BasicObjective objective = new BasicObjective(this, name, null);
        this.registeredObjectives.add(objective);
        return objective;
    }

    @Override
    public @NotNull Set<Team> getTeams() {
        return this.getHandle().getTeams().stream()
                .map(team -> new BasicTeam(this, team.getRegisteredName(), team))
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable Team getTeam(@NotNull String name) {
        ScorePlayerTeam handle = this.getHandle().getTeam(name);
        return handle != null ? new BasicTeam(this, handle.getRegisteredName(), handle) : null;
    }

    @Override
    public @Nullable Team getTeamByEntry(@NotNull String entry) {
        ScorePlayerTeam handle = this.getHandle().getPlayersTeam(entry);
        return handle != null ? new BasicTeam(this, handle.getRegisteredName(), handle) : null;
    }

    @Override
    public @NotNull Team registerNewTeam(@NotNull String name) {
        if (this.getHandle().getTeam(name) != null) {
            throw new IllegalArgumentException("A team with the name \"" + name + "\" already exists");
        }

        return new BasicTeam(this, name, null);
    }
}
