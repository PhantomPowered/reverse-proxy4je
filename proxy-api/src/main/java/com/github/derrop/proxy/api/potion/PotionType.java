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

public enum PotionType {

    WATER(0, 0),
    REGEN(1, 2),
    SPEED(2, 2),
    FIRE_RESISTANCE(3, 1),
    POISON(4, 2),
    INSTANT_HEAL(5, 2),
    NIGHT_VISION(6, 1),
    WEAKNESS(8, 1),
    STRENGTH(9, 2),
    SLOWNESS(10, 1),
    JUMP(11, 2),
    INSTANT_DAMAGE(12, 2),
    WATER_BREATHING(13, 1),
    INVISIBILITY(14, 1);

    private final int damageValue;
    private final int maxLevel;

    PotionType(int damageValue, int maxLevel) {
        this.damageValue = damageValue;
        this.maxLevel = maxLevel;
    }

    public int getDamageValue() {
        return damageValue;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public static PotionType getByDamageValue(int damage) {
        for (PotionType type : PotionType.values()) {
            if (type.damageValue == damage) {
                return type;
            }
        }

        return null;
    }
}
