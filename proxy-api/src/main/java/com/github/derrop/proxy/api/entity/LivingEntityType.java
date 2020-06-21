/*
 * This class has been taken from the Bukkit API
 */
package com.github.derrop.proxy.api.entity;

import com.github.derrop.proxy.api.entity.types.living.EntityLiving;
import com.github.derrop.proxy.api.entity.types.living.animal.Bat;
import com.github.derrop.proxy.api.entity.types.living.animal.Squid;
import com.github.derrop.proxy.api.entity.types.living.animal.ageable.*;
import com.github.derrop.proxy.api.entity.types.living.animal.npc.Villager;
import com.github.derrop.proxy.api.entity.types.living.animal.tamable.Ocelot;
import com.github.derrop.proxy.api.entity.types.living.animal.tamable.Wolf;
import com.github.derrop.proxy.api.entity.types.living.boss.EnderDragon;
import com.github.derrop.proxy.api.entity.types.living.boss.Wither;
import com.github.derrop.proxy.api.entity.types.living.creature.IronGolem;
import com.github.derrop.proxy.api.entity.types.living.creature.Snowman;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.entity.types.living.monster.*;

import java.util.HashMap;
import java.util.Map;

public enum LivingEntityType {

    PLAYER("Player", -2, EntityPlayer.class),
    CREEPER("Creeper", 50, Creeper.class),
    SKELETON("Skeleton", 51, Skeleton.class),
    SPIDER("Spider", 52, Spider.class),
    GIANT("Giant", 53, GiantZombie.class),
    ZOMBIE("Zombie", 54, Zombie.class),
    SLIME("Slime", 55, Slime.class),
    GHAST("Ghast", 56, Ghast.class),
    PIG_ZOMBIE("PigZombie", 57, PigZombie.class),
    ENDER_MAN("Enderman", 58, Enderman.class),
    CAVE_SPIDER("CaveSpider", 59, CaveSpider.class),
    SILVER_FISH("Silverfish", 60, Silverfish.class),
    BLAZE("Balze", 61, Blaze.class),
    LAVA_SLIME("LavaSlime", 62, LavaSlime.class),
    ENDER_DRAGON("EnderDragon", 63, EnderDragon.class),
    WITHER("WitherBoss", 64, Wither.class),
    BAT("Bat", 65, Bat.class),
    WITCH("Witch", 66, Witch.class),
    ENDER_MITE("Endermite", 67, Endermite.class),
    GUARDIAN("Guardian", 68, Guardian.class),
    PIG("Pig", 90, Pig.class),
    SHEEP("Sheep", 91, Sheep.class),
    COW("Cow", 92, Cow.class),
    CHICKEN("Chicken", 93, Chicken.class),
    SQUID("Squid", 94, Squid.class),
    WOLF("Wolf", 95, Wolf.class),
    MUSHROOM_COW("MushroomCow", 96, MushroomCow.class),
    SNOW_MAN("SnowMan", 97, Snowman.class),
    OCELOT("Ozelot", 98, Ocelot.class),
    VILLAGER_GOLEM("VillagerGolem", 99, IronGolem.class),
    HORSE("EntityHorse", 100, Horse.class),
    RABBIT("Rabbit", 101, Rabbit.class),
    VILLAGER("Villager", 120, Villager.class),

    UNKNOWN(null, -1, null);

    private final String name;
    private final short typeId;
    private final Class<? extends EntityLiving> clazz;

    private static final Map<String, LivingEntityType> NAME_MAP = new HashMap<>();
    private static final Map<Short, LivingEntityType> ID_MAP = new HashMap<>();

    static {
        for (LivingEntityType type : values()) {
            if (type.name != null) {
                NAME_MAP.put(type.name.toLowerCase(), type);
            }

            if (type.typeId > 0) {
                ID_MAP.put(type.typeId, type);
            }
        }
    }

    LivingEntityType(String name, int typeId, Class<? extends EntityLiving> clazz) {
        this.name = name;
        this.typeId = (short) typeId;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public short getTypeId() {
        return typeId;
    }

    public static LivingEntityType fromName(String name) {
        if (name == null) {
            return null;
        }

        return NAME_MAP.get(name.toLowerCase());
    }

    public static LivingEntityType fromId(int id) {
        if (id > Short.MAX_VALUE) {
            return null;
        }

        return ID_MAP.getOrDefault((short) id, LivingEntityType.UNKNOWN);
    }
}
