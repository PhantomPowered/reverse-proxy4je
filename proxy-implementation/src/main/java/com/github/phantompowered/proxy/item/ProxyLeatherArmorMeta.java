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

import com.github.phantompowered.proxy.api.item.LeatherArmorMeta;
import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public class ProxyLeatherArmorMeta extends ProxyItemMeta implements LeatherArmorMeta {

    private int color;

    public ProxyLeatherArmorMeta(@NotNull NBTTagCompound source) {
        super(source);

        if (source.hasKey(ItemMetaKeys.DISPLAY, NbtTagNumbers.TAG_COMPOUND)) {
            NBTTagCompound display = source.getCompoundTag(ItemMetaKeys.DISPLAY);
            if (display.hasKey(LeatherArmorMetaKeys.COLOR, NbtTagNumbers.TAG_INT)) {
                this.color = source.getCompoundTag(ItemMetaKeys.DISPLAY).getInteger(LeatherArmorMetaKeys.COLOR);
            }
        }
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound base = super.write();

        if (!base.hasKey(ItemMetaKeys.DISPLAY, NbtTagNumbers.TAG_COMPOUND)) {
            base.setTag(ItemMetaKeys.DISPLAY, new NBTTagCompound());
        }

        base.getCompoundTag(ItemMetaKeys.DISPLAY).setInteger(LeatherArmorMetaKeys.COLOR, this.color);

        return base;
    }

    public interface LeatherArmorMetaKeys {

        String COLOR = "color";

    }
}
