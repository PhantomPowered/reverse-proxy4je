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

import com.github.derrop.proxy.api.nbt.NBTTagCompound;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public abstract class PotionEffect {

    protected final int amplifier;
    protected final int duration;
    protected final PotionEffectType type;
    protected final boolean ambient;
    protected final boolean particles;

    public PotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        Preconditions.checkNotNull(type, "effect type cannot be null");
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particles = particles;
    }

    public PotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
        this(type, duration, amplifier, ambient, true);
    }

    public PotionEffect(PotionEffectType type, int duration, int amplifier) {
        this(type, duration, amplifier, true);
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getDuration() {
        return duration;
    }

    public PotionEffectType getType() {
        return type;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isParticles() {
        return particles;
    }

    public abstract @NotNull NBTTagCompound write();
}
