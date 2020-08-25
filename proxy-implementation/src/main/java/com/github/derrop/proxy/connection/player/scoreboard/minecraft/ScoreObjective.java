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
package com.github.derrop.proxy.connection.player.scoreboard.minecraft;

import com.github.derrop.proxy.connection.player.scoreboard.minecraft.criteria.ScoreObjectiveCriteria;

public class ScoreObjective {

    private final ScoreObjectiveCriteria objectiveCriteria;
    private final Scoreboard scoreboard;
    private final String name;

    private ScoreObjectiveCriteria.RenderType renderType;
    private int displaySlot;
    private String displayName;

    public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, ScoreObjectiveCriteria objectiveCriteriaIn) {
        this.scoreboard = theScoreboardIn;
        this.name = nameIn;
        this.objectiveCriteria = objectiveCriteriaIn;
        this.displayName = nameIn;
        this.renderType = objectiveCriteriaIn.getRenderType();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public ScoreObjectiveCriteria getCriteria() {
        return this.objectiveCriteria;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplaySlot(int displaySlot) {
        this.displaySlot = displaySlot;
    }

    public int getDisplaySlot() {
        return displaySlot;
    }

    public void setDisplayName(String nameIn) {
        this.displayName = nameIn;
    }

    public ScoreObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(ScoreObjectiveCriteria.RenderType type) {
        this.renderType = type;
    }
}
