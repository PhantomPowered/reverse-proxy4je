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
package com.github.phantompowered.proxy.connection.cache.handler.scoreboard;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.Score;
import com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.ScoreObjective;
import com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.ScorePlayerTeam;

public interface ScoreboardHandler {

    void handleObjectiveCreated(ScoreObjective objective);

    void handleObjectiveUpdated(ScoreObjective objective);

    void handleObjectiveUnregistered(ScoreObjective objective);

    void handleScoreUpdated(Score score);

    void handleScoreRemoved(String scoreName, ScoreObjective objective);

    void handleTeamRegistered(ScorePlayerTeam team);

    void handleTeamUpdated(ScorePlayerTeam team);

    void handleTeamEntryAdded(ScorePlayerTeam team, String entry);

    void handleTeamEntryRemoved(ScorePlayerTeam team, String entry);

    void handleTeamUnregistered(ScorePlayerTeam team);

    void handleScoreboardPacket(Packet packet);

}
