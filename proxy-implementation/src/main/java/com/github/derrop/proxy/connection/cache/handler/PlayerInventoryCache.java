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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerWindowItems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryCache implements PacketCacheHandler {

    private static final byte WINDOW_ID = 0; // 0 -> player inventory
    private static final InventoryItem[] EMPTY_INVENTORY = new InventoryItem[45];

    static {
        Arrays.fill(EMPTY_INVENTORY, InventoryItem.NONE);
    }

    private Map<Integer, InventoryItem> itemsBySlot = new HashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.WINDOW_ITEMS, PacketConstants.SET_SLOT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        if (newPacket.getDeserializedPacket() instanceof PacketPlayServerWindowItems) {
            PacketPlayServerWindowItems items = (PacketPlayServerWindowItems) newPacket.getDeserializedPacket();

            if (items.getWindowId() != WINDOW_ID) {
                return;
            }

            for (int slot = 0; slot < items.getItems().length; slot++) {
                InventoryItem item = items.getItems()[slot];
                if (item.getItemId() > 0) {
                    this.itemsBySlot.put(slot, item);
                } else {
                    this.itemsBySlot.remove(slot);
                }
            }
        } else if (newPacket.getDeserializedPacket() instanceof PacketPlayServerSetSlot) {
            PacketPlayServerSetSlot setSlot = (PacketPlayServerSetSlot) newPacket.getDeserializedPacket();
            InventoryItem item = setSlot.getItem();

            if (setSlot.getWindowId() != WINDOW_ID) {
                return;
            }

            if (item.getItemId() > 0) {
                this.itemsBySlot.put(setSlot.getSlot(), item);
            } else {
                this.itemsBySlot.remove(setSlot.getSlot());
            }
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        this.itemsBySlot.keySet().stream().mapToInt(Integer::intValue).max().ifPresent(count -> {
            InventoryItem[] items = new InventoryItem[count + 1];
            for (int slot = 0; slot < items.length; slot++) {
                items[slot] = this.itemsBySlot.getOrDefault(slot, InventoryItem.NONE);
            }
            con.sendPacket(new PacketPlayServerWindowItems(WINDOW_ID, items));
        });
    }

    @Override
    public void onClientSwitch(Player con) {
        con.sendPacket(new PacketPlayServerWindowItems(WINDOW_ID, EMPTY_INVENTORY));
    }
}
