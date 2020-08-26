package com.github.phantompowered.proxy.api.block;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Particle {
    EXPLOSION_NORMAL("explode", 0, true),
    EXPLOSION_LARGE("largeexplode", 1, true),
    EXPLOSION_HUGE("hugeexplosion", 2, true),
    FIREWORKS_SPARK("fireworksSpark", 3, false),
    WATER_BUBBLE("bubble", 4, false),
    WATER_SPLASH("splash", 5, false),
    WATER_WAKE("wake", 6, false),
    SUSPENDED("suspended", 7, false),
    SUSPENDED_DEPTH("depthsuspend", 8, false),
    CRIT("crit", 9, false),
    CRIT_MAGIC("magicCrit", 10, false),
    SMOKE_NORMAL("smoke", 11, false),
    SMOKE_LARGE("largesmoke", 12, false),
    SPELL("spell", 13, false),
    SPELL_INSTANT("instantSpell", 14, false),
    SPELL_MOB("mobSpell", 15, false),
    SPELL_MOB_AMBIENT("mobSpellAmbient", 16, false),
    SPELL_WITCH("witchMagic", 17, false),
    DRIP_WATER("dripWater", 18, false),
    DRIP_LAVA("dripLava", 19, false),
    VILLAGER_ANGRY("angryVillager", 20, false),
    VILLAGER_HAPPY("happyVillager", 21, false),
    TOWN_AURA("townaura", 22, false),
    NOTE("note", 23, false),
    PORTAL("portal", 24, false),
    ENCHANTMENT_TABLE("enchantmenttable", 25, false),
    FLAME("flame", 26, false),
    LAVA("lava", 27, false),
    FOOTSTEP("footstep", 28, false),
    CLOUD("cloud", 29, false),
    REDSTONE("reddust", 30, false),
    SNOWBALL("snowballpoof", 31, false),
    SNOW_SHOVEL("snowshovel", 32, false),
    SLIME("slime", 33, false),
    HEART("heart", 34, false),
    BARRIER("barrier", 35, false),
    ITEM_CRACK("iconcrack_", 36, false, 2),
    BLOCK_CRACK("blockcrack_", 37, false, 1),
    BLOCK_DUST("blockdust_", 38, false, 1),
    WATER_DROP("droplet", 39, false),
    ITEM_TAKE("take", 40, false),
    MOB_APPEARANCE("mobappearance", 41, true),
    ;

    private static final Map<Integer, Particle> PARTICLES = new HashMap<>();
    private static final String[] PARTICLE_NAMES;

    static {
        List<String> list = Lists.newArrayList();

        for (Particle particle : values()) {
            PARTICLES.put(particle.getParticleId(), particle);

            if (!particle.getParticleName().endsWith("_")) {
                list.add(particle.getParticleName());
            }
        }

        PARTICLE_NAMES = list.toArray(new String[0]);
    }

    private final String particleName;
    private final int particleId;
    private final boolean shouldIgnoreRange;
    private final int argumentCount;

    Particle(String name, int particleId, boolean shouldIgnoreRange, int argumentCount) {
        this.particleName = name;
        this.particleId = particleId;
        this.shouldIgnoreRange = shouldIgnoreRange;
        this.argumentCount = argumentCount;
    }

    Particle(String particleNameIn, int particleIDIn, boolean shouldIgnoreRange) {
        this(particleNameIn, particleIDIn, shouldIgnoreRange, 0);
    }

    public static String[] getParticleNames() {
        return PARTICLE_NAMES;
    }

    public static Particle getById(int particleId) {
        return PARTICLES.get(particleId);
    }

    public String getParticleName() {
        return this.particleName;
    }

    public int getParticleId() {
        return this.particleId;
    }

    public int getArgumentCount() {
        return this.argumentCount;
    }

    public boolean getShouldIgnoreRange() {
        return this.shouldIgnoreRange;
    }

    public boolean hasArguments() {
        return this.argumentCount > 0;
    }
}
