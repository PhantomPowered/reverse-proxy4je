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
package com.github.phantompowered.proxy.connection;

import com.github.phantompowered.proxy.api.connection.ServiceInventory;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.connection.cache.handler.HeldItemSlotCache;
import com.github.phantompowered.proxy.connection.cache.handler.PlayerInventoryCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Map;

public class DefaultServiceInventory implements ServiceInventory {

    public static final int HOTBAR_OFFSET = 36;

    private final BasicServiceConnection connection;

    public DefaultServiceInventory(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private PlayerInventoryCache cache() {
        return this.connection.getClient().getPacketCache().getHandler(PlayerInventoryCache.class);
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        return this.cache().getItemsBySlot();
    }

    @Override
    public int getHeldItemSlot() {
        return this.connection.getClient().getPacketCache().getHandler(HeldItemSlotCache.class).getSlot();
    }

    @Override
    public @Nullable ItemStack getHotBarItem(@Range(from = 0, to = 8) int slot) {
        return this.getItem(slot + HOTBAR_OFFSET);
    }

    @Override
    public @Nullable ItemStack getItemInHand() {
        return this.getHotBarItem(this.getHeldItemSlot());
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        return this.getContent().get(slot);
    }
}
