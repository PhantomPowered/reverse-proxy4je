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

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerWindowItems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryCache implements PacketCacheHandler {

    private static final byte WINDOW_ID = 0; // 0 -> player inventory
    private static final ItemStack[] EMPTY_INVENTORY = new ItemStack[45];

    static {
        Arrays.fill(EMPTY_INVENTORY, ItemStack.NONE);
    }

    private Map<Integer, ItemStack> itemsBySlot = new HashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.WINDOW_ITEMS, ProtocolIds.ToClient.Play.SET_SLOT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        if (newPacket instanceof PacketPlayServerWindowItems) {
            PacketPlayServerWindowItems items = (PacketPlayServerWindowItems) newPacket;

            if (items.getWindowId() != WINDOW_ID) {
                return;
            }

            for (int slot = 0; slot < items.getItems().length; slot++) {
                ItemStack item = items.getItems()[slot];
                if (item.getItemId() > 0) {
                    this.itemsBySlot.put(slot, item);
                } else {
                    this.itemsBySlot.remove(slot);
                }
            }
        } else if (newPacket instanceof PacketPlayServerSetSlot) {
            PacketPlayServerSetSlot setSlot = (PacketPlayServerSetSlot) newPacket;
            ItemStack item = setSlot.getItem();

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
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        this.itemsBySlot.keySet().stream().mapToInt(Integer::intValue).max().ifPresent(count -> {
            ItemStack[] items = new ItemStack[count + 1];
            for (int slot = 0; slot < items.length; slot++) {
                items[slot] = this.itemsBySlot.getOrDefault(slot, ItemStack.NONE);
            }
            con.sendPacket(new PacketPlayServerWindowItems(WINDOW_ID, items));
        });
    }

    @Override
    public void onClientSwitch(Player con) {
        con.sendPacket(new PacketPlayServerWindowItems(WINDOW_ID, EMPTY_INVENTORY));
    }
}
