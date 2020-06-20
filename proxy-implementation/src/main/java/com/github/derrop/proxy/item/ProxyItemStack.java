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
package com.github.derrop.proxy.item;

import com.github.derrop.proxy.api.item.ItemMeta;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

public class ProxyItemStack extends ItemStack {

    public static final ItemStack AIR = new ProxyItemStack(0, 0, 0, null);

    public ProxyItemStack(int itemId, int amount, int meta, NBTTagCompound nbt) {
        super(itemId, amount, meta, nbt);
    }

    @Override
    public @Nullable ItemMeta getItemMeta() {
        if (this.itemMeta == null && this.nbt != null) {
            this.itemMeta = ProxyItemMeta.createFromItemId(this.itemId, this.nbt);
        }

        return itemMeta;
    }
}
