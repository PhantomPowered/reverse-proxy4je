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
package com.github.phantompowered.proxy.api.entity;

import com.github.phantompowered.proxy.api.entity.types.ArmorStand;
import com.github.phantompowered.proxy.api.entity.types.Boat;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.entity.types.Firework;
import com.github.phantompowered.proxy.api.entity.types.block.EnderCrystal;
import com.github.phantompowered.proxy.api.entity.types.block.FallingBlock;
import com.github.phantompowered.proxy.api.entity.types.block.TNTPrimed;
import com.github.phantompowered.proxy.api.entity.types.item.*;
import com.github.phantompowered.proxy.api.entity.types.minecart.CommandBlockMinecart;
import com.github.phantompowered.proxy.api.entity.types.minecart.FurnaceMinecart;
import com.github.phantompowered.proxy.api.entity.types.minecart.Minecart;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {

    BOAT("Boat", 1, Boat.class),
    ITEM("Item", 2, Item.class),

    EMPTY_MINE_CART("MinecartRideable", 10, 0, Minecart.class),
    CHEST_MINE_CART("MinecartChest", 10, 1, Minecart.class),
    FURNACE_MINE_CART("MinecartFurnace", 10, 2, FurnaceMinecart.class),
    TNT_MINE_CART("MinecartTNT", 10, 3, Minecart.class),
    HOPPER_MINE_CART("MinecartHopper", 10, 5, Minecart.class),
    MOB_SPAWNER_MINE_CART("MinecartSpawner", 10, 4, Minecart.class),
    COMMAND_BLOCK_MINE_CART("MinecartCommandBlock", 10, 6, CommandBlockMinecart.class),

    TNT_PRIMED("TntPrimed", 50, TNTPrimed.class),
    ENDER_CRYSTAL("EnderCrystal", 51, EnderCrystal.class),
    ARROW("Arrow", 60, Arrow.class),
    SNOWBALL("Snowball", 61, Snowball.class),
    EGG("Egg", 62, Egg.class),
    FIRE_BALL("FireBall", 63, Fireball.class),
    SMALL_FIRE_BALL("SmallFireBall", 64, SmallFireball.class),
    ENDER_PEARL("EnderPearl", 65, EnderPearl.class),
    WITHER_SKULL("WitherSkull", 66, WitherSkull.class),
    FALLING_BLOCK("FallingBlock", 70, FallingBlock.class),
    ITEM_FRAME("ItemFrame", 71, ItemFrame.class),
    ENDER_SIGNAL("EnderSignal", 72, EnderSignal.class),
    POTION("Potion", 73, Potion.class),
    THROWN_EXP_BOTTLE("ThrownExpBottle", 75, ThrownExpBottle.class),
    FIREWORK("Firework", 76, Firework.class),
    LEASH("Leash", 77, Leash.class),
    ARMOR_STAND("ArmorStand", 78, ArmorStand.class),
    FISHING_HOOK("FishingHook", 90, FishingHook.class),

    UNKNOWN("Unknown", -1, null);

    private static final Map<String, EntityType> NAME_MAP = new HashMap<>();

    static {
        for (EntityType type : values()) {
            if (type.name != null) {
                NAME_MAP.put(type.name.toLowerCase(), type);
            }
        }
    }

    private final String name;
    private final short typeId;
    private final short subId;
    private final Class<? extends Entity> clazz;

    EntityType(String name, int typeId, Class<? extends Entity> clazz) {
        this(name, typeId, -1, clazz);
    }

    EntityType(String name, int typeId, int subId, Class<? extends Entity> clazz) {
        this.name = name;
        this.typeId = (short) typeId;
        this.subId = (short) subId;
        this.clazz = clazz;
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
            if (subId == 0 && (value.getSubId() == -1 || value.getSubId() == 0) && value.getTypeId() == id) {
                return value;
            }

            if (subId != 0 && value.getSubId() != -1 && value.getSubId() == subId && value.getTypeId() == id) {
                return value;
            }

            if (subId != 0 && value.getSubId() == -1 && value.getTypeId() == id) {
                return value;
            }
        }

        return UNKNOWN;
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

    public Class<? extends Entity> getClazz() {
        return clazz;
    }
}
