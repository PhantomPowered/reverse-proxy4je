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
package com.github.phantompowered.proxy.api.item;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import com.google.errorprone.annotations.concurrent.LazyInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemStack {

    protected final int amount;
    protected final int meta;
    protected final NBTTagCompound nbt;
    protected Material material;
    @LazyInit
    protected @Nullable ItemMeta itemMeta;

    public ItemStack(Material material, int amount, int meta, NBTTagCompound nbt) {
        this.material = material;
        this.amount = amount;
        this.meta = meta;
        this.nbt = nbt;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public int getMeta() {
        return meta;
    }

    @Nullable
    public NBTTagCompound getNbt() {
        return this.getItemMeta() == null ? this.nbt : this.getItemMeta().write();
    }

    public boolean hasItemMeta() {
        return this.itemMeta != null || this.nbt != null;
    }

    @Nullable
    public abstract ItemMeta getItemMeta();

    public void setItemMeta(@NotNull ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    @Override
    public String toString() {
        return "ItemStack{"
                + "material=" + material
                + ", amount=" + amount
                + ", meta=" + meta
                + ", nbt=" + nbt
                + '}';
    }
}
