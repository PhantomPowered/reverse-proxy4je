package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.inventory.SetSlot;
import com.github.derrop.proxy.connection.cache.packet.inventory.WindowItems;
import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.connection.UserConnection;

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
        if (newPacket.getDeserializedPacket() instanceof WindowItems) {
            WindowItems items = (WindowItems) newPacket.getDeserializedPacket();

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
        } else if (newPacket.getDeserializedPacket() instanceof SetSlot) {
            SetSlot setSlot = (SetSlot) newPacket.getDeserializedPacket();
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
            con.sendPacket(new WindowItems(WINDOW_ID, items));
        });
    }

    @Override
    public void onClientSwitch(UserConnection con) {
        con.sendPacket(new WindowItems(WINDOW_ID, EMPTY_INVENTORY));
    }
}
