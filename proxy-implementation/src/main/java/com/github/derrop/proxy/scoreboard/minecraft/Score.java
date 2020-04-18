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
package com.github.derrop.proxy.scoreboard.minecraft;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Score
{
    public static final Comparator<Score> scoreComparator = (p_compare_1_, p_compare_2_) -> p_compare_1_.getScorePoints() > p_compare_2_.getScorePoints() ? 1 : (p_compare_1_.getScorePoints() < p_compare_2_.getScorePoints() ? -1 : p_compare_2_.getPlayerName().compareToIgnoreCase(p_compare_1_.getPlayerName()));
    private final Scoreboard theScoreboard;
    private final ScoreObjective theScoreObjective;
    private final String scorePlayerName;
    private int scorePoints;
    private boolean locked;
    private boolean field_178818_g;

    public Score(Scoreboard theScoreboardIn, ScoreObjective theScoreObjectiveIn, String scorePlayerNameIn)
    {
        this.theScoreboard = theScoreboardIn;
        this.theScoreObjective = theScoreObjectiveIn;
        this.scorePlayerName = scorePlayerNameIn;
        this.field_178818_g = true;
    }

    public void increseScore(int amount)
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.setScorePoints(this.getScorePoints() + amount);
        }
    }

    public void decreaseScore(int amount)
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.setScorePoints(this.getScorePoints() - amount);
        }
    }

    public void func_96648_a()
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.increseScore(1);
        }
    }

    public int getScorePoints()
    {
        return this.scorePoints;
    }

    public void setScorePoints(int points)
    {
        int i = this.scorePoints;
        this.scorePoints = points;

        if (i != points || this.field_178818_g)
        {
            this.field_178818_g = false;
            this.getScoreScoreboard().func_96536_a(this);
        }
    }

    public ScoreObjective getObjective()
    {
        return this.theScoreObjective;
    }

    /**
     * Returns the name of the player this score belongs to
     */
    public String getPlayerName()
    {
        return this.scorePlayerName;
    }

    public Scoreboard getScoreScoreboard()
    {
        return this.theScoreboard;
    }

    public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public void func_96651_a(List<UUID> p_96651_1_)
    {
        this.setScorePoints(this.theScoreObjective.getCriteria().getScoreForPlayers(p_96651_1_));
    }
}
