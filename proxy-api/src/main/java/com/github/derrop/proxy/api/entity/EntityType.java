/*
 * This class has been taken from the Bukkit API
 */
package com.github.derrop.proxy.api.entity;

import java.util.HashMap;
import java.util.Map;

// TODO some type Ids are not correct
public enum EntityType {

    /**
     * An item resting on the ground.
     */
    DROPPED_ITEM("Item", 1, false),
    /**
     * An experience orb.
     */
    EXPERIENCE_ORB("XPOrb", 2),
    /**
     * A leash attached to a fencepost.
     */
    LEASH_HITCH("LeashKnot", 77),
    /**
     * A painting on a wall.
     */
    PAINTING("Painting", 9),
    /**
     * An arrow projectile; may get stuck in the ground.
     */
    ARROW("Arrow", 10),
    /**
     * A flying snowball.
     */
    SNOWBALL("Snowball", 11),
    /**
     * A flying large fireball, as thrown by a Ghast for example.
     */
    FIREBALL("Fireball", 12),
    /**
     * A flying small fireball, such as thrown by a Blaze or player.
     */
    SMALL_FIREBALL("SmallFireball", 13),
    /**
     * A flying ender pearl.
     */
    ENDER_PEARL("ThrownEnderpearl", 14),
    /**
     * An ender eye signal.
     */
    ENDER_SIGNAL("EyeOfEnderSignal", 72),
    /**
     * A flying experience bottle.
     */
    THROWN_EXP_BOTTLE("ThrownExpBottle", 75),
    /**
     * An item frame on a wall.
     */
    ITEM_FRAME("ItemFrame", 71),
    /**
     * A flying wither skull projectile.
     */
    WITHER_SKULL("WitherSkull", 19),
    /**
     * Primed TNT that is about to explode.
     */
    PRIMED_TNT("PrimedTnt", 20),
    /**
     * A block that is going to or is about to fall.
     */
    FALLING_BLOCK("FallingSand", 70, false),
    FIREWORK("FireworksRocketEntity", 76, false),
    ARMOR_STAND("ArmorStand", 78, false),
    MINECART_COMMAND("MinecartCommandBlock", 40),
    /**
     * A placed boat.
     */
    BOAT("Boat", 41),
    MINECART("MinecartRideable", 42),
    MINECART_CHEST("MinecartChest", 43),
    MINECART_FURNACE("MinecartFurnace", 44),
    MINECART_TNT("MinecartTNT", 45),
    MINECART_HOPPER("MinecartHopper", 46),
    MINECART_MOB_SPAWNER("MinecartMobSpawner", 47),
    CREEPER("Creeper", 50),
    SKELETON("Skeleton", 51),
    SPIDER("Spider", 52),
    GIANT("Giant", 53),
    ZOMBIE("Zombie", 54),
    SLIME("Slime", 55),
    GHAST("Ghast", 56),
    PIG_ZOMBIE("PigZombie", 57),
    ENDERMAN("Enderman", 58),
    CAVE_SPIDER("CaveSpider", 59),
    SILVERFISH("Silverfish", 60),
    BLAZE("Blaze", 61),
    MAGMA_CUBE("LavaSlime", 62),
    ENDER_DRAGON("EnderDragon", 63),
    WITHER("WitherBoss", 64),
    BAT("Bat", 65),
    WITCH("Witch", 66),
    ENDERMITE("Endermite", 67),
    GUARDIAN("Guardian", 68),
    PIG("Pig", 90),
    SHEEP("Sheep", 91),
    COW("Cow", 92),
    CHICKEN("Chicken", 93),
    SQUID("Squid", 94),
    WOLF("Wolf", 95),
    MUSHROOM_COW("MushroomCow", 96),
    SNOWMAN("SnowMan", 97),
    OCELOT("Ozelot", 98),
    IRON_GOLEM("VillagerGolem", 99),
    HORSE("EntityHorse", 100),
    RABBIT("Rabbit", 101),
    VILLAGER("Villager", 120),
    ENDER_CRYSTAL("EnderCrystal", 200),
    /**
     * A flying splash potion.
     */
    SPLASH_POTION(null, 73, false),
    /**
     * A flying chicken egg.
     */
    EGG(null, -1, false),
    /**
     * A fishing line and bobber.
     */
    FISHING_HOOK(null, -1, false),
    /**
     * A bolt of lightning.
     */
    LIGHTNING(null, -1, false),
    WEATHER(null, -1, false),
    PLAYER(null, -1, false),
    COMPLEX_PART(null, -1, false),
    /**
     * An unknown entity without an Entity Class
     */
    UNKNOWN(null, -1, false);

    private String name;
    private short typeId;
    private boolean independent;

    private static final Map<String, EntityType> NAME_MAP = new HashMap<String, EntityType>();
    private static final Map<Short, EntityType> ID_MAP = new HashMap<Short, EntityType>();

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

    private EntityType(String name, int typeId) {
        this(name, typeId, true);
    }

    private EntityType(String name, int typeId, boolean independent) {
        this.name = name;
        this.typeId = (short) typeId;
        this.independent = independent;
    }

    /**
     *
     * @return the entity type's name
     * @deprecated Magic value
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     *
     * @return the raw type id 
     * @deprecated Magic value
     */
    @Deprecated
    public short getTypeId() {
        return typeId;
    }

    /**
     *
     * @param name the entity type's name
     * @return the matching entity type or null
     * @deprecated Magic value
     */
    @Deprecated
    public static EntityType fromName(String name) {
        if (name == null) {
            return null;
        }
        return NAME_MAP.get(name.toLowerCase());
    }

    /**
     *
     * @param id the raw type id
     * @return the matching entity type or null
     * @deprecated Magic value
     */
    @Deprecated
    public static EntityType fromId(int id) {
        if (id > Short.MAX_VALUE) {
            return null;
        }
        return ID_MAP.get((short) id);
    }

    /**
     * @return False if the entity type cannot be spawned
     */
    public boolean isSpawnable() {
        return independent;
    }

}
