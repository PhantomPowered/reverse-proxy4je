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
