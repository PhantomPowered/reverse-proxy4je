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
package com.github.derrop.proxy.api.player;

import java.util.HashMap;
import java.util.Map;

public enum GameMode {

    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator"),
    ;

    private final int id;
    private final String name;

    GameMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAdventure() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isSurvivalOrAdventure() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode getById(int id) {
        return BY_ID.getOrDefault(id, SURVIVAL);
    }

    private static final Map<Integer, GameMode> BY_ID = new HashMap<>();

    static {
        for (GameMode value : values()) {
            BY_ID.put(value.id, value);
        }
    }
}
