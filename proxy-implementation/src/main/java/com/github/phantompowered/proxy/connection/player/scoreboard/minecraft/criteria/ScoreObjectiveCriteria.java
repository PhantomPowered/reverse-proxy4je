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
package com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.criteria;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ScoreObjectiveCriteria {

    Map<String, ScoreObjectiveCriteria> INSTANCES = Maps.newHashMap();
    ScoreObjectiveCriteria DUMMY = new ScoreDummyCriteria("dummy");
    ScoreObjectiveCriteria TRIGGER = new ScoreDummyCriteria("trigger");
    ScoreObjectiveCriteria DEATH_COUNT = new ScoreDummyCriteria("deathCount");
    ScoreObjectiveCriteria PLAYER_KILL_COUNT = new ScoreDummyCriteria("playerKillCount");
    ScoreObjectiveCriteria TOTAL_KILL_COUNT = new ScoreDummyCriteria("totalKillCount");

    String getName();

    int getScoreForPlayers(List<UUID> players);

    boolean isReadOnly();

    RenderType getRenderType();

    enum RenderType {

        INTEGER("integer"),
        HEARTS("hearts");

        private static final Map<String, RenderType> TYPE_BY_KEY = Maps.newHashMap();

        static {
            for (RenderType type : values()) {
                TYPE_BY_KEY.put(type.getKey(), type);
            }
        }

        private final String key;

        RenderType(String key) {
            this.key = key;
        }

        public static RenderType getTypeFromKey(String p_178795_0_) {
            RenderType renderType = TYPE_BY_KEY.get(p_178795_0_);
            return renderType == null ? INTEGER : renderType;
        }

        public String getKey() {
            return this.key;
        }
    }
}
