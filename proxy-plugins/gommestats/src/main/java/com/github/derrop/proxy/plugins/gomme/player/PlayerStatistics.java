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
package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerStatistics implements Comparable<PlayerStatistics> {

    private final GommeServerType gameMode;
    private final Map<String, String> stats;
    private final int rank;
    private final boolean privateStats;

    public PlayerStatistics(GommeServerType gameMode, Map<String, String> stats, int rank, boolean privateStats) {
        this.gameMode = gameMode;
        this.stats = stats;
        this.rank = rank;
        this.privateStats = privateStats;
    }

    public GommeServerType getGameMode() {
        return this.gameMode;
    }

    public Map<String, String> getStats() {
        return this.stats;
    }

    public int getRank() {
        return this.rank;
    }

    public boolean isPrivateStats() {
        return this.privateStats;
    }

    @Override
    public int compareTo(@NotNull PlayerStatistics o) {
        return Integer.compare(this.rank, o.rank);
    }
}
