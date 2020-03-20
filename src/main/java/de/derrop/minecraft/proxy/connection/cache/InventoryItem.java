package de.derrop.minecraft.proxy.connection.cache;

import com.flowpowered.nbt.CompoundTag;

public class InventoryItem {

    public static final InventoryItem NONE = new InventoryItem(false, -1, (byte) -1, null);

    private boolean present;
    private int itemId;
    private byte count;
    private CompoundTag nbt;

    public InventoryItem(boolean present, int itemId, byte count, CompoundTag nbt) {
        this.present = present;
        this.itemId = itemId;
        this.count = count;
        this.nbt = nbt;
    }

    public boolean isPresent() {
        return present;
    }

    public int getItemId() {
        return itemId;
    }

    public byte getCount() {
        return count;
    }

    public CompoundTag getNbt() {
        return nbt;
    }
}
