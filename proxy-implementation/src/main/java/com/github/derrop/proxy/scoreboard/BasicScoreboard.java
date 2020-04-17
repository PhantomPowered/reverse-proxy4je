package com.github.derrop.proxy.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.*;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.scoreboard.DisplaySlot;
import com.github.derrop.proxy.api.scoreboard.Objective;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.connection.cache.handler.scoreboard.ScoreboardCache;
import com.github.derrop.proxy.connection.cache.handler.scoreboard.ScoreboardHandler;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardDisplay;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardObjective;
import com.github.derrop.proxy.scoreboard.minecraft.Score;
import com.github.derrop.proxy.scoreboard.minecraft.ScoreObjective;
import com.github.derrop.proxy.scoreboard.minecraft.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BasicScoreboard implements Scoreboard, ScoreboardHandler {

    private final ServiceConnection connection;
    private final ScoreboardCache cache;

    private final Collection<BasicObjective> registeredObjectives = new CopyOnWriteArrayList<>();

    public BasicScoreboard(ServiceConnection connection, ScoreboardCache cache) {
        this.connection = connection;
        this.cache = cache;

        cache.setHandler(this);
    }

    public void removeObjective(BasicObjective objective) {
        this.registeredObjectives.remove(objective);
    }

    @NotNull
    public com.github.derrop.proxy.scoreboard.minecraft.Scoreboard getHandle() {
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
    public @NotNull Team registerNewTeam(@NotNull String name) {
        if (this.getHandle().getTeam(name) != null) {
            throw new IllegalArgumentException("A team with the name \"" + name + "\" already exists");
        }
        return new BasicTeam(this, name, null);
    }

    private void callEvent(Event event) {
        this.connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
    }

    @Override
    public void handleObjectiveCreated(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveRegisterEvent(this.connection, new BasicObjective(this, objective.getName(), objective)));
    }

    @Override
    public void handleObjectiveUpdated(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveUpdateEvent(this.connection, new BasicObjective(this, objective.getName(), objective)));
    }

    @Override
    public void handleObjectiveUnregistered(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveUnregisterEvent(this.connection, new BasicObjective(this, objective.getName(), objective)));
    }

    @Override
    public void handleScoreUpdated(Score score) {
        this.callEvent(new ScoreboardScoreSetEvent(this.connection, new BasicScore(
                this, new BasicObjective(this, score.getObjective().getName(), score.getObjective()),
                score.getPlayerName(), score.getScorePoints()
        )));
    }

    @Override
    public void handleScoreRemoved(String scoreName, ScoreObjective objective) {
        this.callEvent(new ScoreboardScoreSetEvent(this.connection, new BasicScore(
                this, objective == null ? null : new BasicObjective(this, objective.getName(), objective),
                scoreName, -1
        )));
    }

    @Override
    public void handleTeamRegistered(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamRegisterEvent(this.connection, new BasicTeam(this, team.getRegisteredName(), team)));
    }

    @Override
    public void handleTeamUpdated(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamUpdateEvent(this.connection, new BasicTeam(this, team.getRegisteredName(), team)));
    }

    @Override
    public void handleTeamEntryAdded(ScorePlayerTeam team, String entry) {
        this.callEvent(new ScoreboardTeamEntryAddEvent(this.connection, new BasicTeam(this, team.getRegisteredName(), team), entry));
    }

    @Override
    public void handleTeamEntryRemoved(ScorePlayerTeam team, String entry) {
        this.callEvent(new ScoreboardTeamEntryRemoveEvent(this.connection, new BasicTeam(this, team.getRegisteredName(), team), entry));
    }

    @Override
    public void handleTeamUnregistered(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamUnregisterEvent(this.connection, new BasicTeam(this, team.getRegisteredName(), team)));
    }

    @Override
    public void handleScoreboardPacket(Packet packet) {
        if (packet instanceof PacketPlayServerScoreboardObjective || packet instanceof PacketPlayServerScoreboardDisplay) {
            for (BasicObjective objective : this.registeredObjectives) {
                objective.ensureRegistered();
            }
        }
    }
}
