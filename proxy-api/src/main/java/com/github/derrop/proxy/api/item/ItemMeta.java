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

import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.google.common.collect.Multimap;
import net.kyori.text.TranslatableComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemMeta {

    @NotNull Set<ItemFlag> getItemFlags();

    void addItemFlags(@NonNls ItemFlag... itemFlags);

    void removeItemFlags(@NonNls ItemFlag... itemFlags);

    boolean hasItemFlag(@NotNull ItemFlag flag);

    @NotNull Map<Enchantment, Integer> getEnchantments();

    void setEnchantments(@NotNull Map<Enchantment, Integer> enchantments);

    @NotNull Multimap<Attribute, AttributeModifier> getModifiers();

    void setModifiers(@NotNull Multimap<Attribute, AttributeModifier> modifiers);

    @NotNull List<TranslatableComponent> getLore();

    void setLore(@NotNull List<TranslatableComponent> lore);

    @Nullable TranslatableComponent getDisplayName();

    void setDisplayName(@NotNull TranslatableComponent displayName);

    @NotNull TranslatableComponent getLocName();

    void setLocName(@NotNull TranslatableComponent locName);

    int getRepairCost();

    void setRepairCost(int repairCost);

    boolean isUnbreakable();

    void setUnbreakable(boolean unbreakable);

    int getDamage();

    void setDamage(int damage);

    int getCustomModelData();

    void setCustomModelData(int customModelData);

    @NotNull NBTTagCompound getBlockData();

    void setBlockData(@NotNull NBTTagCompound blockData);

    @NotNull NBTTagCompound write();
}
