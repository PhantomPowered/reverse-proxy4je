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
package com.github.derrop.proxy.api.entity;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {

    BOAT("Boat", 1),
    ITEM("Item", 2),

    EMPTY_MINE_CART("MinecartRideable", 10, 0),
    CHEST_MINE_CART("MinecartChest", 10, 1),
    FURNANCE_MINE_CART("MinecartFurnace", 10, 2),
    TNT_MINE_CART("MinecartTNT", 10, 3),
    HOPPER_MINE_CART("MinecartHopper", 10, 5),
    MOB_SPAWNER_MINE_CART("MinecartSpawner", 10, 4),
    COMMAND_BLOCK_MINE_CART("MinecartCommandBlock", 10, 6),

    TNT_PRIMED("TntPrimed", 50),
    ENDER_CRYSTAL("EnderCrystal", 51),
    ARROW("Arrow", 60),
    SNOWBALL("Snowball", 61),
    EGG("Egg", 62),
    FIRE_BALL("FireBall", 63),
    SMALL_FIRE_BALL("SmallFireBall", 64),
    ENDER_PEARL("EnderPearl", 65),
    WITHER_SKULL("WitherSkull", 66),
    FALLING_BLOCK("FallingBlock", 70),
    ITEM_FRAME("ItemFrame", 71),
    ENDER_SIGNAL("EnderSignal", 72),
    POTION("Potion", 73),
    THROWN_EXP_BOTTLE("ThrownExpBottle", 75),
    FIREWORK("Firework", 76),
    LEASH("Leash", 77),
    ARMOR_STAND("ArmorStand", 78),
    FISHING_HOOK("FishingHook", 90),

    UNKNOWN("Unknown", -1);

    private final String name;
    private final short typeId;
    private final short subId;

    private static final Map<String, EntityType> NAME_MAP = new HashMap<>();

    static {
        for (EntityType type : values()) {
            if (type.name != null) {
                NAME_MAP.put(type.name.toLowerCase(), type);
            }
        }
    }

    EntityType(String name, int typeId) {
        this(name, typeId, -1);
    }

    EntityType(String name, int typeId, int subId) {
        this.name = name;
        this.typeId = (short) typeId;
        this.subId = (short) subId;
    }

    public String getName() {
        return name;
    }

    public short getTypeId() {
        return typeId;
    }

    public short getSubId() {
        return subId;
    }

    public static EntityType fromName(String name) {
        if (name == null) {
            return null;
        }

        return NAME_MAP.get(name.toLowerCase());
    }

    public static EntityType fromId(int id) {
        return fromId(id, -1);
    }

    public static EntityType fromId(int id, int subId) { // subId = -1 -> any sub id is ok
        if (id > Short.MAX_VALUE) {
            return null;
        }

        if (subId > Short.MAX_VALUE) {
            subId = (short) -1;
        }

        for (EntityType value : values()) {
            if (subId == -1 && value.getTypeId() == id) {
                return value;
            }

            if (subId != -1 && value.getSubId() != -1 && value.getSubId() == subId && value.getTypeId() == id) {
                return value;
            }
        }

        return UNKNOWN;
    }
}
