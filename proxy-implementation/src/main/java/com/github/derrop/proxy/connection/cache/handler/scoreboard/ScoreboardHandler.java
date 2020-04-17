package com.github.derrop.proxy.connection.cache.handler.scoreboard;

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.scoreboard.minecraft.Score;
import com.github.derrop.proxy.scoreboard.minecraft.ScoreObjective;
import com.github.derrop.proxy.scoreboard.minecraft.ScorePlayerTeam;

public interface ScoreboardHandler {

    void handleObjectiveCreated(ScoreObjective objective);

    void handleObjectiveUpdated(ScoreObjective objective);

    void handleObjectiveUnregistered(ScoreObjective objective);

    void handleScoreUpdated(Score score);

    void handleScoreRemoved(String scoreName, ScoreObjective objective);

    void handleTeamRegistered(ScorePlayerTeam team);

    void handleTeamUpdated(ScorePlayerTeam team);

    void handleTeamUnregistered(ScorePlayerTeam team);

    void handleScoreboardPacket(Packet packet);

}
