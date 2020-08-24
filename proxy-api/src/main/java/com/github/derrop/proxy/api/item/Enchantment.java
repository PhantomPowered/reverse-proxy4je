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
package com.github.derrop.proxy.api.item;

import com.github.derrop.proxy.api.util.MinecraftKey;

import java.util.HashMap;
import java.util.Map;

public enum Enchantment {

    PROTECTION(0, MinecraftKey.forValue("protection")),
    FIRE_PROTECTION(1, MinecraftKey.forValue("fire_protection")),
    FEATHER_FALLING(2, MinecraftKey.forValue("feather_falling")),
    BLAST_PROTECTION(3, MinecraftKey.forValue("blast_protection")),
    PROJECTILE_PROTECTION(4, MinecraftKey.forValue("projectile_protection")),
    RESPIRATION(5, MinecraftKey.forValue("respiration")),
    AQUA_AFFINITY(6, MinecraftKey.forValue("aqua_affinity")),
    THORNS(7, MinecraftKey.forValue("thorns")),
    DEPTH_STRIDER(8, MinecraftKey.forValue("depth_strider")),
    FROST_WALKER(9, MinecraftKey.forValue("frost_walker")), // 1.9 only

    SHARPNESS(16, MinecraftKey.forValue("sharpness")),
    SMITE(17, MinecraftKey.forValue("smite")),
    BANE_OF_ARTHROPODS(18, MinecraftKey.forValue("bane_of_arthropods")),
    KNOCKBACK(19, MinecraftKey.forValue("knockback")),
    FIRE_ASPECT(20, MinecraftKey.forValue("fire_aspect")),
    LOOTING(21, MinecraftKey.forValue("looting")),

    EFFICIENCY(32, MinecraftKey.forValue("efficiency")),
    SILK_TOUCH(33, MinecraftKey.forValue("silk_touch")),
    UNBREAKING(34, MinecraftKey.forValue("unbreaking")),
    FORTUNE(35, MinecraftKey.forValue("fortune")),

    POWER(48, MinecraftKey.forValue("power")),
    PUNCH(49, MinecraftKey.forValue("punch")),
    FLAME(50, MinecraftKey.forValue("flame")),
    INFINITY(51, MinecraftKey.forValue("infinity")),

    LUCK_OF_THE_SEA(61, MinecraftKey.forValue("luck_of_the_sea")),
    LURE(62, MinecraftKey.forValue("lure")),

    MENDING(70, MinecraftKey.forValue("mending")), // 1.9 only

    UNKNOWN(-1, null);

    private static final Map<Short, Enchantment> BY_ID = new HashMap<>();

    static {
        for (Enchantment value : values()) {
            BY_ID.put(value.getId(), value);
        }
    }

    private final short id;
    private final MinecraftKey key;

    Enchantment(int id, MinecraftKey key) {
        this.id = (short) id;
        this.key = key;
    }

    public short getId() {
        return id;
    }

    public MinecraftKey getKey() {
        return key;
    }

    public static Enchantment getById(int id) {
        if (id > Short.MAX_VALUE) {
            return null;
        }

        return BY_ID.get((short) id);
    }

    public static Enchantment getOrDefaultById(int id) {
        if (id > Short.MAX_VALUE) {
            return UNKNOWN;
        }

        return BY_ID.getOrDefault((short) id, UNKNOWN);
    }
}
