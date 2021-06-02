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
import com.github.phantompowered.proxy.api.player.inventory.ClickType;
import com.github.phantompowered.proxy.connection.cache.handler.HeldItemSlotCache;
import com.github.phantompowered.proxy.connection.cache.handler.PlayerInventoryCache;
import com.github.phantompowered.proxy.protocol.play.client.inventory.PacketPlayClientClickWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultServiceInventory implements ServiceInventory {

    public static final int HOTBAR_OFFSET = 36;

    private final BasicServiceConnection connection;

    private final Map<Short, CompletableFuture<Void>> pendingTransactions = new HashMap<>();
    private final AtomicInteger transactionCounter = new AtomicInteger(5000);
    private int openWindowId;

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
    public int getOpenWindowId() {
        return this.openWindowId;
    }

    public void setOpenWindowId(int openWindowId) {
        this.openWindowId = openWindowId;
    }

    @Override
    public @Nullable ItemStack getHotBarItem(@Range(from = 0, to = 8) int slot) {
        return this.getPlayerItem(slot + HOTBAR_OFFSET);
    }

    @Override
    public @Nullable ItemStack getItemInHand() {
        return this.getHotBarItem(this.getHeldItemSlot());
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        return this.getContent().get(slot);
    }

    @Override
    public @Nullable ItemStack getPlayerItem(int slot) {
        return this.cache().getPlayerItemsBySlot().get(slot);
    }

    @Override
    public CompletableFuture<Void> performClick(int windowId, ClickType type, int slot) {
        int action = this.transactionCounter.incrementAndGet();
        if (action == Short.MAX_VALUE - 100) {
            this.transactionCounter.set(5000);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        this.pendingTransactions.put((short) action, future);

        ItemStack item = this.getItem(slot);
        this.connection.sendPacket(new PacketPlayClientClickWindow(windowId, slot, (short) action, item, type));

        return future;
    }

    @Override
    public boolean completeTransaction(short actionNumber) {
        if (!this.pendingTransactions.containsKey(actionNumber)) {
            return false;
        }

        this.pendingTransactions.remove(actionNumber).complete(null);
        return true;
    }
}
