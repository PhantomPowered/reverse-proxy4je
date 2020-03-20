package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.InventoryItem;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.WindowItems;
import net.md_5.bungee.netty.ChannelWrapper;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventoryCache implements PacketCacheHandler {

    private static final byte WINDOW_ID = 0; // 0 -> player inventory

    private Map<Integer, InventoryItem> itemsBySlot = new HashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.WINDOW_ITEMS};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        WindowItems items = new WindowItems();
        items.read(newPacket.getPacketData());

        if (items.getWindowId() != WINDOW_ID) {
            return;
        }
        for (int slot = 0; slot < items.getItems().length; slot++) {
            InventoryItem item = items.getItems()[slot];
            if (item.isPresent()) {
                this.itemsBySlot.put(slot, item);
            } else {
                this.itemsBySlot.remove(slot);
            }
        }
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        this.itemsBySlot.keySet().stream().mapToInt(Integer::intValue).max().ifPresent(count -> {
            InventoryItem[] items = new InventoryItem[count];
            for (int slot = 0; slot < items.length; slot++) {
                items[slot] = this.itemsBySlot.getOrDefault(slot, InventoryItem.NONE);
            }
            ch.write(new WindowItems(WINDOW_ID, items));
        });
    }
}
