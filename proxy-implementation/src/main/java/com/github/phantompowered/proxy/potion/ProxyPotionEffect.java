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
package com.github.phantompowered.proxy.potion;

import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import com.github.phantompowered.proxy.api.potion.PotionEffect;
import com.github.phantompowered.proxy.api.potion.PotionEffectType;
import com.github.phantompowered.proxy.item.ProxyPotionMeta;
import org.jetbrains.annotations.NotNull;

public class ProxyPotionEffect extends PotionEffect {

    public ProxyPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        super(type, duration, amplifier, ambient, particles);
    }

    public ProxyPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
        super(type, duration, amplifier, ambient);
    }

    public ProxyPotionEffect(PotionEffectType type, int duration, int amplifier) {
        super(type, duration, amplifier);
    }

    @NotNull
    public static PotionEffect fromNbt(@NotNull NBTTagCompound compound) {
        PotionEffectType type = PotionEffectType.getById(compound.getByte(ProxyPotionMeta.PotionMetaKeys.ID));
        byte amplifier = compound.getByte(ProxyPotionMeta.PotionMetaKeys.AMPLIFIER);
        int duration = compound.getInteger(ProxyPotionMeta.PotionMetaKeys.DURATION);
        boolean ambient = compound.getBoolean(ProxyPotionMeta.PotionMetaKeys.AMBIENT);
        boolean showParticles = compound.getBoolean(ProxyPotionMeta.PotionMetaKeys.SHOW_PARTICLES);

        return new ProxyPotionEffect(type, duration, amplifier, ambient, showParticles);
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setByte(ProxyPotionMeta.PotionMetaKeys.ID, (byte) this.type.getId());
        compound.setByte(ProxyPotionMeta.PotionMetaKeys.AMPLIFIER, (byte) this.amplifier);
        compound.setInteger(ProxyPotionMeta.PotionMetaKeys.DURATION, this.duration);
        compound.setBoolean(ProxyPotionMeta.PotionMetaKeys.AMBIENT, this.ambient);
        compound.setBoolean(ProxyPotionMeta.PotionMetaKeys.SHOW_PARTICLES, this.particles);

        return compound;
    }
}
