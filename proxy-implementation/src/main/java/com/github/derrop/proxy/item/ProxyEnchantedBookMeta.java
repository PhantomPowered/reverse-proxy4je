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

import com.github.derrop.proxy.api.item.EnchantedBookMeta;
import com.github.derrop.proxy.api.item.Enchantment;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProxyEnchantedBookMeta extends ProxyItemMeta implements EnchantedBookMeta {

    static {
        ProxyItemMeta.HANDLED.add(EnchantedBookMetaKeys.STORED_ENCHANTMENTS);
    }

    public ProxyEnchantedBookMeta(@NotNull NBTTagCompound source) {
        super(source);


        if (source.hasKey(EnchantedBookMetaKeys.STORED_ENCHANTMENTS)) {
            this.enchantments.putAll(super.buildEnchantments(source, EnchantedBookMetaKeys.STORED_ENCHANTMENTS));
        }
    }

    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    @Override
    public boolean hasEnchantments() {
        return this.enchantments.size() > 0;
    }

    @NotNull
    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public void removeEnchantment(@NotNull Enchantment enchantment) {
        this.enchantments.remove(enchantment);
    }

    @Override
    public void setEnchantment(@NotNull Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound compound = super.write();

        if (this.hasEnchantments()) {
            this.writeEnchantments(this.enchantments, compound, EnchantedBookMetaKeys.STORED_ENCHANTMENTS);
        }

        return compound;
    }

    public interface EnchantedBookMetaKeys {

        String STORED_ENCHANTMENTS = "StoredEnchantments";
    }
}
