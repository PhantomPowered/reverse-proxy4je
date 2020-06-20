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
package com.github.derrop.proxy.api.potion;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum PotionEffectType {

    SPEED(1),
    SLOWNESS(2),
    FAST_DIGGING(3),
    SLOW_DIGGING(4),
    INCREASE_DAMAGE(5),
    HEAL(6),
    HARM(7),
    JUMP(8),
    CONFUSION(9),
    REGENERATION(10),
    DAMAGE_RESISTANCE(11),
    FIRE_RESISTANCE(12),
    WATER_BREATHING(13),
    INVISIBILITY(14),
    BLINDNESS(15),
    NIGHT_VISION(16),
    HUNGER(17),
    WEAKNESS(18),
    POISON(19),
    WITHER(20),
    HEALTH_BOOST(21),
    ABSORPTION(22),
    SATURATION(23);

    private final short id;

    PotionEffectType(int id) {
        this.id = (short) id;
    }

    public short getId() {
        return id;
    }

    private static final Map<Short, PotionEffectType> TYPES = new HashMap<>();

    static {
        for (PotionEffectType value : values()) {
            TYPES.put(value.getId(), value);
        }
    }

    @Nullable
    public static PotionEffectType getById(int id) {
        return id > Short.MAX_VALUE ? null : TYPES.get((short) id);
    }
}
