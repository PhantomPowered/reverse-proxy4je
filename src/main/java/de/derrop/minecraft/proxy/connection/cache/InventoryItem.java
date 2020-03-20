package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.util.nbt.NBTTagCompound;

public class InventoryItem {

    public static final InventoryItem NONE = new InventoryItem(0, 0, 0, null);

    private int itemId;
    private int amount;
    private int meta;
    private NBTTagCompound nbt;

    public InventoryItem(int itemId, int amount, int meta, NBTTagCompound nbt) {
        this.itemId = itemId;
        this.amount = amount;
        this.meta = meta;
        this.nbt = nbt;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getMeta() {
        return meta;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }
}
