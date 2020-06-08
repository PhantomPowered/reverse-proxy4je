/*
 * This class has been taken from the Bukkit API
 */
package com.github.derrop.proxy.api.entity;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {

    ITEM("Item", 1),
    EXPERIENCE_ORB("XPOrb", 2),
    EGG("ThrownEgg", 7),
    LEASH_KNOT("LeashKnot", 8),
    PAINTING("Painting", 9),
    ARROW("Arrow", 10),
    SNOW_BALL("Snowball", 11),
    FIRE_BALL("Fireball", 12),
    SMALL_FIRE_BALL("SmallFireball", 13),
    ENDER_PEARL("ThrownEnderpearl", 14),
    ENDER_EYE("EyeOfEnderSignal", 15),
    POTION("ThrownPotion", 16),
    EXP_BOTTLE("ThrownExpBottle", 17),
    ITEM_FRAME("ItemFrame", 18),
    WITHER_SKULL("WitherSkull", 19),
    PRIMED_TNT("PrimedTnt", 20),
    FALLING_SAND("FallingSand", 21),
    FIRE_WORK_ROCKET("FireworksRocketEntity", 22),
    ARMOR_STAND("ArmorStand", 30),
    BOAT("Boat", 41),

    EMPTY_MINE_CART("MinecartRideable", 42),
    CHEST_MINE_CART("MinecartChest", 43),
    FURNANCE_MINE_CART("MinecartFurnace", 44),
    TNT_MINE_CART("MinecartTNT", 45),
    HOPPER_MINE_CART("MinecartHopper", 46),
    MOB_SPAWNER_MINE_CART("MinecartSpawner", 47),
    COMMAND_BLOCK_MINE_CART("MinecartCommandBlock", 48),

    MOB("Mob", 48),
    MONSTER("Monster", 49),
    CREEPER("Creeper", 50),
    SKELETON("Skeleton", 51),
    SPIDER("Spider", 52),
    GIANT("Giant", 53),
    ZOMBIE("Zombie", 54),
    SLIME("Slime", 55),
    GHAST("Ghast", 56),
    PIG_ZOMBIE("PigZombie", 57),
    ENDER_MAN("Enderman", 58),
    CAVE_SPIDER("CaveSpider", 59),
    SILVER_FISH("Silverfish", 60),
    BLAZE("Balze", 61),
    LAVA_SLIME("LavaSlime", 62),
    ENDER_DRAGON("EnderDragon", 63),
    WITHER("WitherBoss", 64),
    BAT("Bat", 65),
    WITCH("Witch", 66),
    ENDER_MITE("Endermite", 67),
    GUARDIAN("Guardian", 68),
    PIG("Pig", 90),
    SHEEP("Sheep", 91),
    COW("Cow", 92),
    CHICKEN("Chicken", 93),
    SQUID("Squid", 94),
    WOLF("Wolf", 95),
    MUSHROOM_COW("MushroomCow", 96),
    SNOW_MAN("SnowMan", 97),
    OZELOT("Ozelot", 98),
    VILLAGER_GOLEM("VillagerGolem", 99),
    HORSE("EntityHorse", 100),
    RABBIT("Rabbit", 101),
    VILLAGER("Villager", 120),
    ENDER_CRYSTAL("EnderCrystal", 200),
    PLAYER(null, -1),

    UNKNOWN(null, -1);

    private final String name;
    private final short typeId;

    private static final Map<String, EntityType> NAME_MAP = new HashMap<>();
    private static final Map<Short, EntityType> ID_MAP = new HashMap<>();

    static {
        for (EntityType type : values()) {
            if (type.name != null) {
                NAME_MAP.put(type.name.toLowerCase(), type);
            }

            if (type.typeId > 0) {
                ID_MAP.put(type.typeId, type);
            }
        }
    }

    EntityType(String name, int typeId) {
        this.name = name;
        this.typeId = (short) typeId;
    }

    public String getName() {
        return name;
    }

    public short getTypeId() {
        return typeId;
    }

    public static EntityType fromName(String name) {
        if (name == null) {
            return null;
        }

        return NAME_MAP.get(name.toLowerCase());
    }

    public static EntityType fromId(int id) {
        if (id > Short.MAX_VALUE) {
            return null;
        }

        return ID_MAP.get((short) id);
    }
}
