package com.github.derrop.proxy.api.entity.player.inventory;

public enum EquipmentSlot {

    HAND("Hand"),
    BOOTS("Boots"),
    LEGGINGS("Leggings"),
    CHESTPLATE("Chestplate"),
    HELMET("Helmet");

    private String formattedName;

    EquipmentSlot(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public int getSlotId() {
        return super.ordinal();
    }

    public static EquipmentSlot getById(int id) {
        EquipmentSlot[] values = values();
        return id >= 0 && id < values.length ? values[id] : null;
    }

}
