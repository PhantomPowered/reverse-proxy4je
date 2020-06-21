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

import com.google.common.base.Preconditions;

public class BrewedPotion {

    private boolean extended = false;
    private boolean splash = false;
    private int level = 1;
    private int name = -1;
    private PotionType type;

    public BrewedPotion(int name) {
        this(PotionType.getByDamageValue(name & 15));
        this.name = name & 63;
        if ((name & 15) == 0) {
            this.type = null;
        }
    }

    public BrewedPotion(PotionType type) {
        this.type = type;
        if (type != null) {
            this.name = type.getDamageValue();
        }

        if (type == null || type == PotionType.WATER) {
            this.level = 0;
        }
    }

    public BrewedPotion(int level, PotionType type) {
        this(type);

        Preconditions.checkNotNull(type, "Unable to create leveled potion from null type");
        Preconditions.checkArgument(type != PotionType.WATER, "Water does not have a level");
        Preconditions.checkArgument((level > 0 && level < 3) || level > type.getMaxLevel(), "Level must be 1 or 2 (max: %d)", type.getMaxLevel());

        this.level = level;
    }

    public BrewedPotion(boolean splash, int level, PotionType type) {
        this(level, type);
        this.splash = splash;
    }

    public BrewedPotion splash() {
        setSplash(true);
        return this;
    }

    public void setSplash(boolean splash) {
        this.splash = splash;
    }

    public BrewedPotion extended() {
        setExtended(true);
        return this;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isExtended() {
        return extended;
    }

    public boolean isSplash() {
        return splash;
    }

    public int getLevel() {
        return level;
    }

    public int getName() {
        return name;
    }

    public PotionType getType() {
        return type;
    }

    public short toPotionValue() {
        short result;
        if (type == PotionType.WATER) {
            return 0;
        }

        if (type == null) {
            result = (short) (name == 0 ? 8192 : name);
        } else {
            result = (short) (level - 1);
            result <<= 5;
            result |= (short) type.getDamageValue();
        }

        if (splash) {
            result |= 16384;
        }

        if (extended) {
            result |= 64;
        }

        return result;
    }

    public static BrewedPotion fromPotionValue(int value) {
        BrewedPotion brewedPotion;
        PotionType type = PotionType.getByDamageValue(value & 15);

        if (type == null || type == PotionType.WATER) {
            brewedPotion = new BrewedPotion(value & 63);
        } else {
            int level = ((value & 32) >> 5) + 1;
            brewedPotion = new BrewedPotion(level, type);
        }

        if ((value & 16384) > 0) {
            brewedPotion = brewedPotion.splash();
        }

        if (type == null && (value & 64) > 0) {
            brewedPotion = brewedPotion.extended();
        }

        return brewedPotion;
    }
}
