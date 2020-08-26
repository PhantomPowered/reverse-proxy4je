package com.github.phantompowered.proxy.api.entity;

import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.entity.types.Firework;
import com.github.phantompowered.proxy.api.entity.types.living.EntityLiving;
import com.github.phantompowered.proxy.api.entity.types.living.animal.Animal;
import com.github.phantompowered.proxy.api.entity.types.living.animal.Squid;
import com.github.phantompowered.proxy.api.entity.types.living.animal.ageable.Horse;
import com.github.phantompowered.proxy.api.entity.types.living.animal.ageable.Rabbit;
import com.github.phantompowered.proxy.api.entity.types.living.animal.ageable.Sheep;
import com.github.phantompowered.proxy.api.entity.types.living.animal.npc.Villager;
import com.github.phantompowered.proxy.api.entity.types.living.animal.tamable.Tameable;
import com.github.phantompowered.proxy.api.entity.types.living.animal.tamable.Wolf;
import com.github.phantompowered.proxy.api.entity.types.living.creature.IronGolem;
import com.github.phantompowered.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.phantompowered.proxy.api.entity.types.living.monster.Guardian;
import com.github.phantompowered.proxy.api.entity.types.living.monster.Witch;
import com.github.phantompowered.proxy.api.entity.types.living.monster.Zombie;
import com.github.phantompowered.proxy.api.entity.types.minecart.Minecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum EntityStatusType {

    RABBIT_JUMP(1, Rabbit.class),
    DAMAGE(2, EntityLiving.class),
    DEATH(3, EntityLiving.class),
    IRON_GOLEM_THROW(4, IronGolem.class),
    HORSE_TAME_SUCCESS(6, Horse.class),
    HORSE_TAME_FAILED(7, Horse.class),
    WOLF_TAME_SUCCESS(6, Tameable.class),
    WOLF_TAME_FAILED(7, Tameable.class),
    WOLF_SHAKING(8, Wolf.class),
    ITEM_USE_FINISH(9, null), // only called for the client himself
    RESET_SHEEP_TIMER(10, Sheep.class),
    IGNITE_TNT_MINECART(10, Minecart.class),
    RESET_IRON_GOLEM_ROSE_TICK(11, IronGolem.class),
    VILLAGER_HEARTS(12, Villager.class),
    VILLAGER_HIT(13, Villager.class),
    VILLAGER_HAPPY(14, Villager.class),
    SPAWN_WITCH_PARTICLES(15, Witch.class),
    ZOMBIE_REMEDY(16, Zombie.class),
    MAKE_FIREWORKS(17, Firework.class),
    LOVE_SUCCESS(18, Animal.class), // cows, sheep, pigs, ocelots, wolf + flesh
    RESET_SQUID_ROTATION(19, Squid.class),
    EXPLOSION_PARTICLE(20, EntityLiving.class), // silverfish block enter, silverfish block exit, (minecart) mob spawner spawned insentient
    GUARDIAN_SOUND(21, Guardian.class),
    REDUCE_DEBUG_INFO(22, null),
    INCREASE_DEBUG_INFO(23, null);

    private static final Map<Byte, EntityStatusType> PLAYER_STATUS_BY_ID = new HashMap<>();

    static {
        for (EntityStatusType value : values()) {
            if (value.entityClass == null || value.entityClass.isAssignableFrom(EntityPlayer.class)) {
                PLAYER_STATUS_BY_ID.put(value.id, value);
            }
        }
    }

    private final byte id;
    private final Class<? extends Entity> entityClass;

    EntityStatusType(int id, @Nullable Class<? extends Entity> entityClass) {
        this.id = (byte) id;
        this.entityClass = entityClass;
    }

    public static EntityStatusType ofOtherEntity(@NotNull Class<? extends Entity> entityClass, byte id) {
        for (EntityStatusType value : values()) {
            if (value.id == id && value.entityClass != null && value.entityClass.isAssignableFrom(entityClass)) {
                return value;
            }
        }
        return null;
    }

    public static EntityStatusType ofSelfPlayer(byte id) {
        return PLAYER_STATUS_BY_ID.get(id);
    }

    public byte getId() {
        return this.id;
    }

    public boolean isOnlySentForOwnEntity() {
        return this.entityClass == null;
    }

    @Nullable
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

}