package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.InventoryItem;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.SetSlot;
import de.derrop.minecraft.proxy.connection.cache.packet.WindowItems;
import net.md_5.bungee.netty.ChannelWrapper;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryCache implements PacketCacheHandler {

    private static final byte WINDOW_ID = 0; // 0 -> player inventory

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
    public void sendCached(ChannelWrapper ch) {
        this.itemsBySlot.keySet().stream().mapToInt(Integer::intValue).max().ifPresent(count -> {
            InventoryItem[] items = new InventoryItem[count + 1];
            for (int slot = 0; slot < items.length; slot++) {
                items[slot] = this.itemsBySlot.getOrDefault(slot, InventoryItem.NONE);
            }
            ch.write(new WindowItems(WINDOW_ID, items));
        });
    }
}
