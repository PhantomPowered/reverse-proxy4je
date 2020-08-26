package com.github.phantompowered.proxy.api.player.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum EquipmentSlot {

    HAND("Hand", "mainhand"),
    BOOTS("Boots", "feet"),
    LEGGINGS("Leggings", "legs"),
    CHESTPLATE("Chestplate", "chest"),
    HELMET("Helmet", "head");

    private static final Map<String, EquipmentSlot> BY_NAME = new HashMap<>();

    static {
        for (EquipmentSlot value : values()) {
            BY_NAME.put(value.slotNameNms, value);
        }
    }

    private final String formattedName;
    private final String slotNameNms;

    EquipmentSlot(String formattedName, String slotNameNms) {
        this.formattedName = formattedName;
        this.slotNameNms = slotNameNms;
    }

    public static EquipmentSlot getById(int id) {
        EquipmentSlot[] values = values();
        return id >= 0 && id < values.length ? values[id] : null;
    }

    public static EquipmentSlot getByName(@NotNull String name) {
        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getSlotNameNms() {
        return slotNameNms;
    }

    public int getSlotId() {
        return super.ordinal();
    }
}
