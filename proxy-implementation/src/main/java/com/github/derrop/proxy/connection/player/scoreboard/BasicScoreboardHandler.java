package com.github.derrop.proxy.connection.player.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.*;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.connection.cache.handler.scoreboard.ScoreboardHandler;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.Score;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.ScoreObjective;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.ScorePlayerTeam;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardDisplay;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardObjective;

public class BasicScoreboardHandler implements ScoreboardHandler {

    private final ServiceConnection connection;
    private final BasicScoreboard scoreboard;

    public BasicScoreboardHandler(ServiceConnection connection, BasicScoreboard scoreboard) {
        this.connection = connection;
        this.scoreboard = scoreboard;
    }

    @Override
    public void handleScoreboardPacket(Packet packet) {
        if (packet instanceof PacketPlayServerScoreboardObjective || packet instanceof PacketPlayServerScoreboardDisplay) {
            for (BasicObjective objective : this.scoreboard.getRegisteredObjectives()) {
                objective.ensureRegistered();
            }
        }
    }

    private void callEvent(Event event) {
        this.connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
    }

    @Override
    public void handleObjectiveCreated(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveRegisterEvent(this.connection, new BasicObjective(this.scoreboard, objective.getName(), objective)));
    }

    @Override
    public void handleObjectiveUpdated(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveUpdateEvent(this.connection, new BasicObjective(this.scoreboard, objective.getName(), objective)));
    }

    @Override
    public void handleObjectiveUnregistered(ScoreObjective objective) {
        this.callEvent(new ScoreboardObjectiveUnregisterEvent(this.connection, new BasicObjective(this.scoreboard, objective.getName(), objective)));
    }

    @Override
    public void handleScoreUpdated(Score score) {
        this.callEvent(new ScoreboardScoreSetEvent(this.connection, new BasicScore(
                this.scoreboard, new BasicObjective(this.scoreboard, score.getObjective().getName(), score.getObjective()),
                score.getPlayerName(), score.getScorePoints()
        )));
    }

    @Override
    public void handleScoreRemoved(String scoreName, ScoreObjective objective) {
        this.callEvent(new ScoreboardScoreSetEvent(this.connection, new BasicScore(
                this.scoreboard, objective == null ? null : new BasicObjective(this.scoreboard, objective.getName(), objective),
                scoreName, -1
        )));
    }

    @Override
    public void handleTeamRegistered(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamRegisterEvent(this.connection, new BasicTeam(this.scoreboard, team.getRegisteredName(), team)));
    }

    @Override
    public void handleTeamUpdated(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamUpdateEvent(this.connection, new BasicTeam(this.scoreboard, team.getRegisteredName(), team)));
    }

    @Override
    public void handleTeamEntryAdded(ScorePlayerTeam team, String entry) {
        this.callEvent(new ScoreboardTeamEntryAddEvent(this.connection, new BasicTeam(this.scoreboard, team.getRegisteredName(), team), entry));
    }

    @Override
    public void handleTeamEntryRemoved(ScorePlayerTeam team, String entry) {
        this.callEvent(new ScoreboardTeamEntryRemoveEvent(this.connection, new BasicTeam(this.scoreboard, team.getRegisteredName(), team), entry));
    }

    @Override
    public void handleTeamUnregistered(ScorePlayerTeam team) {
        this.callEvent(new ScoreboardTeamUnregisterEvent(this.connection, new BasicTeam(this.scoreboard, team.getRegisteredName(), team)));
    }
    
}
