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
package com.github.phantompowered.proxy.item;

import com.github.phantompowered.proxy.api.item.PotionMeta;
import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import com.github.phantompowered.proxy.api.nbt.NBTTagList;
import com.github.phantompowered.proxy.api.potion.BrewedPotion;
import com.github.phantompowered.proxy.api.potion.PotionEffect;
import com.github.phantompowered.proxy.api.potion.PotionEffectType;
import com.github.phantompowered.proxy.potion.ProxyPotionEffect;
import com.google.errorprone.annotations.concurrent.LazyInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyPotionMeta extends ProxyItemMeta implements PotionMeta {

    static {
        HANDLED.addAll(Arrays.asList(
                PotionMetaKeys.AMBIENT,
                PotionMetaKeys.AMPLIFIER,
                PotionMetaKeys.DURATION,
                PotionMetaKeys.ID,
                PotionMetaKeys.POTION_EFFECTS,
                PotionMetaKeys.SHOW_PARTICLES
        ));
    }

    private final int data;
    private final List<PotionEffect> customEffects = new ArrayList<>();
    @LazyInit
    private @Nullable BrewedPotion potion;

    public ProxyPotionMeta(@NotNull NBTTagCompound source, int data) {
        super(source);

        if (source.hasKey(PotionMetaKeys.POTION_EFFECTS, NbtTagNumbers.TAG_LIST)) {
            NBTTagList list = source.getTagList(PotionMetaKeys.POTION_EFFECTS, NbtTagNumbers.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                this.customEffects.add(ProxyPotionEffect.fromNbt(list.getCompoundTagAt(i)));
            }
        }

        this.data = data;
    }

    @Override
    public boolean hasCustomEffects() {
        return this.customEffects.size() > 0;
    }

    @Override
    public @NotNull List<PotionEffect> getCustomEffects() {
        return this.customEffects;
    }

    @Override
    public boolean addCustomEffect(@NotNull PotionEffect effect, boolean overwrite) {
        int index = this.indexOfEffect(effect.getType());
        if (index == -1) {
            this.customEffects.add(effect);
        } else {
            if (!overwrite) {
                return false;
            }

            PotionEffect old = this.customEffects.get(index);
            if (old.getAmplifier() == effect.getAmplifier() && old.getDuration() == effect.getDuration() && old.isAmbient() == effect.isAmbient()) {
                return false;
            }

            this.customEffects.set(index, effect);
        }

        return true;
    }

    @Override
    public boolean removeCustomEffect(@NotNull PotionEffectType type) {
        return this.customEffects.removeIf(potionEffect -> potionEffect.getType() == type);
    }

    @Override
    public boolean hasCustomEffect(@NotNull PotionEffectType type) {
        return this.indexOfEffect(type) >= 0;
    }

    @Override
    public boolean setMainEffect(@NotNull PotionEffectType type) {
        int index = this.indexOfEffect(type);
        if (index == -1 || index == 0) {
            return false;
        }

        PotionEffect effect = this.customEffects.get(0);
        this.customEffects.set(0, customEffects.get(index));
        this.customEffects.set(index, effect);
        return true;
    }

    @Override
    public boolean clearCustomEffects() {
        boolean empty = this.customEffects.isEmpty();
        this.customEffects.clear();
        return !empty;
    }

    @Override
    public @Nullable BrewedPotion asPotion() {
        if (this.potion == null) {
            this.potion = BrewedPotion.fromPotionValue(this.data);
        }

        return this.potion;
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound compound = super.write();

        if (this.hasCustomEffects()) {
            compound.setTag(PotionMetaKeys.POTION_EFFECTS, new NBTTagList());
            for (PotionEffect customEffect : this.customEffects) {
                compound.getTagList(PotionMetaKeys.POTION_EFFECTS, NbtTagNumbers.TAG_COMPOUND).appendTag(customEffect.write());
            }
        }

        return compound;
    }

    private int indexOfEffect(@NotNull PotionEffectType type) {
        for (int i = 0; i < this.customEffects.size(); i++) {
            if (this.customEffects.get(i).getType().equals(type)) {
                return i;
            }
        }

        return -1;
    }

    public interface PotionMetaKeys {

        String AMPLIFIER = "Amplifier";
        String AMBIENT = "Ambient";
        String DURATION = "Duration";
        String SHOW_PARTICLES = "ShowParticles";
        String POTION_EFFECTS = "CustomPotionEffects";
        String ID = "Id";
    }
}
