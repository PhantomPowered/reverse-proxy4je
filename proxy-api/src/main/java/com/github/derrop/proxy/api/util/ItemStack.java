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
package com.github.derrop.proxy.api.util;

import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;

public class ItemStack {

    public static final ItemStack NONE = new ItemStack(0, 0, 0, null);

    private int itemId;
    private int amount;
    private int meta;
    private NBTTagCompound nbt;

    public ItemStack(int itemId, int amount, int meta, NBTTagCompound nbt) {
        this.itemId = itemId;
        this.amount = amount;
        this.meta = meta;
        this.nbt = nbt;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getMeta() {
        return meta;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    @Override
    public String toString() {
        return "ItemStack{" +
                "itemId=" + itemId +
                ", amount=" + amount +
                ", meta=" + meta +
                ", nbt=" + nbt +
                '}';
    }
}
