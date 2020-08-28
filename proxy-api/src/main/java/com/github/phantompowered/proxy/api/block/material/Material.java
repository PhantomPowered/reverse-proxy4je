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
package com.github.phantompowered.proxy.api.block.material;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.Map;

public enum Material {

    AIR(0, 0, MaterialCategory.TRANSPARENT),
    STONE(1, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    GRASS(2, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    DIRT(3, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    COBBLESTONE(4, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    PLANKS(5, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    SAPLING(6, MaterialCategory.TRANSPARENT),
    BEDROCK(7, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    WATER(8),
    STATIONARY_WATER(9),
    LAVA(10),
    STATIONARY_LAVA(11),
    SAND(12, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.GRAVITY),
    GRAVEL(13, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.GRAVITY),
    GOLD_ORE(14, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    IRON_ORE(15, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    COAL_ORE(16, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    LOG(17, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    LEAVES(18, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    SPONGE(19, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    GLASS(20, MaterialCategory.SOLID),
    LAPIS_ORE(21, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    LAPIS_BLOCK(22, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    DISPENSER(23, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SANDSTONE(24, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    NOTE_BLOCK(25, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE),
    BED_BLOCK(26, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    POWERED_RAIL(27, MaterialCategory.TRANSPARENT),
    DETECTOR_RAIL(28, MaterialCategory.TRANSPARENT),
    PISTON_STICKY_BASE(29, MaterialCategory.SOLID),
    WEB(30),
    LONG_GRASS(31, MaterialCategory.TRANSPARENT, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    DEAD_BUSH(32, MaterialCategory.TRANSPARENT, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    PISTON_BASE(33, MaterialCategory.SOLID),
    PISTON_EXTENSION(34, MaterialCategory.SOLID),
    WOOL(35, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    PISTON_MOVING_PIECE(36, MaterialCategory.SOLID),
    YELLOW_FLOWER(37, MaterialCategory.TRANSPARENT, MaterialCategory.BURNABLE),
    FLOWER(38, MaterialCategory.TRANSPARENT, MaterialCategory.BURNABLE),
    BROWN_MUSHROOM(39, MaterialCategory.TRANSPARENT),
    RED_MUSHROOM(40, MaterialCategory.TRANSPARENT),
    GOLD_BLOCK(41, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    IRON_BLOCK(42, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    DOUBLE_STONE_SLAB(43, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    STONE_SLAB(44, MaterialCategory.SOLID),
    BRICK(45, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    TNT(46, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    BOOKSHELF(47, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    MOSSY_COBBLESTONE(48, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    OBSIDIAN(49, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    TORCH(50, MaterialCategory.TRANSPARENT),
    FIRE(51, MaterialCategory.TRANSPARENT),
    MOB_SPAWNER(52, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    WOOD_STAIRS(53, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    CHEST(54, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    REDSTONE_WIRE(55, MaterialCategory.TRANSPARENT),
    DIAMOND_ORE(56, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    DIAMOND_BLOCK(57, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    WORKBENCH(58, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE),
    CROPS(59, MaterialCategory.TRANSPARENT),
    SOIL(60, MaterialCategory.SOLID),
    FURNACE(61, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    BURNING_FURNACE(62, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SIGN_POST(63, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    WOODEN_DOOR(64, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    LADDER(65, MaterialCategory.TRANSPARENT),
    RAILS(66, MaterialCategory.TRANSPARENT),
    COBBLESTONE_STAIRS(67, MaterialCategory.SOLID),
    WALL_SIGN(68, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    LEVER(69, MaterialCategory.TRANSPARENT),
    STONE_PLATE(70, MaterialCategory.SOLID),
    IRON_DOOR_BLOCK(71, MaterialCategory.SOLID, MaterialCategory.DOOR),
    WOOD_PLATE(72, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    REDSTONE_ORE(73, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    GLOWING_REDSTONE_ORE(74, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    REDSTONE_TORCH_OFF(75, MaterialCategory.TRANSPARENT),
    REDSTONE_TORCH_ON(76, MaterialCategory.TRANSPARENT),
    STONE_BUTTON(77, MaterialCategory.TRANSPARENT),
    SNOW(78, MaterialCategory.TRANSPARENT),
    ICE(79, MaterialCategory.SOLID),
    SNOW_BLOCK(80, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    CACTUS(81, MaterialCategory.SOLID),
    CLAY(82, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SUGAR_CANE_BLOCK(83, MaterialCategory.TRANSPARENT),
    JUKEBOX(84, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE),
    FENCE(85, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    PUMPKIN(86, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    NETHERRACK(87, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SOUL_SAND(88, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    GLOWSTONE(89, MaterialCategory.SOLID),
    PORTAL(90, MaterialCategory.TRANSPARENT),
    JACK_O_LANTERN(91, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    CAKE_BLOCK(92, MaterialCategory.SOLID),
    DIODE_BLOCK_OFF(93, MaterialCategory.TRANSPARENT),
    DIODE_BLOCK_ON(94, MaterialCategory.TRANSPARENT),
    STAINED_GLASS(95, MaterialCategory.SOLID),
    TRAP_DOOR(96, MaterialCategory.WOODEN, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    MONSTER_EGGS(97, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SMOOTH_BRICK(98, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    HUGE_MUSHROOM_1(99, MaterialCategory.WOODEN, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE),
    HUGE_MUSHROOM_2(100, MaterialCategory.WOODEN, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE),
    IRON_FENCE(101, MaterialCategory.SOLID),
    THIN_GLASS(102, MaterialCategory.SOLID),
    MELON_BLOCK(103, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    PUMPKIN_STEM(104, MaterialCategory.TRANSPARENT),
    MELON_STEM(105, MaterialCategory.TRANSPARENT),
    VINE(106, MaterialCategory.TRANSPARENT, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    FENCE_GATE(107, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE, MaterialCategory.FENCE_GATE),
    BRICK_STAIRS(108, MaterialCategory.SOLID),
    SMOOTH_STAIRS(109, MaterialCategory.SOLID),
    MYCEL(110, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    WATER_LILY(111, MaterialCategory.TRANSPARENT),
    NETHER_BRICK(112, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    NETHER_FENCE(113, MaterialCategory.SOLID, MaterialCategory.FENCE),
    NETHER_BRICK_STAIRS(114, MaterialCategory.SOLID),
    NETHER_WARTS(115, MaterialCategory.TRANSPARENT),
    ENCHANTMENT_TABLE(116, MaterialCategory.SOLID),
    BREWING_STAND(117, MaterialCategory.SOLID),
    CAULDRON(118, MaterialCategory.SOLID),
    ENDER_PORTAL(119, MaterialCategory.TRANSPARENT),
    ENDER_PORTAL_FRAME(120, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    ENDER_STONE(121, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    DRAGON_EGG(122, MaterialCategory.SOLID),
    REDSTONE_LAMP_OFF(123, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    REDSTONE_LAMP_ON(124, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    WOOD_DOUBLE_STEP(125, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    WOOD_STEP(126, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    COCOA(127, MaterialCategory.TRANSPARENT),
    SANDSTONE_STAIRS(128, MaterialCategory.SOLID),
    EMERALD_ORE(129, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    ENDER_CHEST(130, MaterialCategory.SOLID),
    TRIPWIRE_HOOK(131, MaterialCategory.TRANSPARENT),
    TRIPWIRE(132, MaterialCategory.TRANSPARENT),
    EMERALD_BLOCK(133, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SPRUCE_WOOD_STAIRS(134, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    BIRCH_WOOD_STAIRS(135, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    JUNGLE_WOOD_STAIRS(136, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    COMMAND(137, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    BEACON(138, MaterialCategory.SOLID),
    COBBLE_WALL(139, MaterialCategory.SOLID, MaterialCategory.FENCE),
    FLOWER_POT(140, MaterialCategory.TRANSPARENT),
    CARROT(141, MaterialCategory.TRANSPARENT),
    POTATO(142, MaterialCategory.TRANSPARENT),
    WOOD_BUTTON(143, MaterialCategory.TRANSPARENT),
    SKULL(144, MaterialCategory.TRANSPARENT),
    ANVIL(145, MaterialCategory.SOLID, MaterialCategory.GRAVITY),
    TRAPPED_CHEST(146, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    GOLD_PLATE(147, MaterialCategory.SOLID),
    IRON_PLATE(148, MaterialCategory.SOLID),
    REDSTONE_COMPARATOR_OFF(149, MaterialCategory.TRANSPARENT),
    REDSTONE_COMPARATOR_ON(150, MaterialCategory.TRANSPARENT),
    DAYLIGHT_DETECTOR(151, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    REDSTONE_BLOCK(152, MaterialCategory.SOLID),
    QUARTZ_ORE(153, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.ORE),
    HOPPER(154, MaterialCategory.SOLID),
    QUARTZ_BLOCK(155, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    QUARTZ_STAIRS(156, MaterialCategory.SOLID),
    ACTIVATOR_RAIL(157, MaterialCategory.TRANSPARENT),
    DROPPER(158, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    STAINED_CLAY(159, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    STAINED_GLASS_PANE(160, MaterialCategory.SOLID),
    LEAVES_2(161, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    LOG_2(162, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    ACACIA_STAIRS(163, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    DARK_OAK_STAIRS(164, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    SLIME_BLOCK(165, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    BARRIER(166, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    IRON_TRAPDOOR(167, MaterialCategory.SOLID),
    PRISMARINE(168, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    SEA_LANTERN(169, MaterialCategory.SOLID),
    HAY_BLOCK(170, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.BURNABLE),
    CARPET(171, MaterialCategory.TRANSPARENT, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    HARD_CLAY(172, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    COAL_BLOCK(173, MaterialCategory.SOLID, MaterialCategory.OCCLUDING, MaterialCategory.BURNABLE),
    PACKED_ICE(174, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    DOUBLE_PLANT(175, MaterialCategory.TRANSPARENT, MaterialCategory.FLAMMABLE, MaterialCategory.BURNABLE),
    STANDING_BANNER(176, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    WALL_BANNER(177, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    DAYLIGHT_DETECTOR_INVERTED(178, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE),
    RED_SANDSTONE(179, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    RED_SANDSTONE_STAIRS(180, MaterialCategory.SOLID),
    DOUBLE_STONE_SLAB2(181, MaterialCategory.SOLID, MaterialCategory.OCCLUDING),
    STONE_SLAB2(182, MaterialCategory.SOLID),
    SPRUCE_FENCE_GATE(183, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE_GATE, MaterialCategory.BURNABLE),
    BIRCH_FENCE_GATE(184, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE_GATE, MaterialCategory.BURNABLE),
    JUNGLE_FENCE_GATE(185, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE_GATE, MaterialCategory.BURNABLE),
    DARK_OAK_FENCE_GATE(186, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE_GATE, MaterialCategory.BURNABLE),
    ACACIA_FENCE_GATE(187, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE_GATE, MaterialCategory.BURNABLE),
    SPRUCE_FENCE(188, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    BIRCH_FENCE(189, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    JUNGLE_FENCE(190, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    DARK_OAK_FENCE(191, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    ACACIA_FENCE(192, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.FENCE, MaterialCategory.BURNABLE),
    SPRUCE_DOOR(193, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    BIRCH_DOOR(194, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    JUNGLE_DOOR(195, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    ACACIA_DOOR(196, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    DARK_OAK_DOOR(197, MaterialCategory.SOLID, MaterialCategory.FLAMMABLE, MaterialCategory.DOOR, MaterialCategory.WOODEN),
    // ----- Item Separator -----
    IRON_SPADE(256, 1, 250),
    IRON_PICKAXE(257, 1, 250),
    IRON_AXE(258, 1, 250),
    FLINT_AND_STEEL(259, 1, 64),
    APPLE(260, MaterialCategory.EDIBLE),
    BOW(261, 1, 384),
    ARROW(262),
    COAL(263),
    DIAMOND(264),
    IRON_INGOT(265),
    GOLD_INGOT(266),
    IRON_SWORD(267, 1, 250, MaterialCategory.SWORD),
    WOOD_SWORD(268, 1, 59, MaterialCategory.SWORD),
    WOOD_SPADE(269, 1, 59),
    WOOD_PICKAXE(270, 1, 59),
    WOOD_AXE(271, 1, 59),
    STONE_SWORD(272, 1, 131, MaterialCategory.SWORD),
    STONE_SPADE(273, 1, 131),
    STONE_PICKAXE(274, 1, 131),
    STONE_AXE(275, 1, 131),
    DIAMOND_SWORD(276, 1, 1561, MaterialCategory.SWORD),
    DIAMOND_SPADE(277, 1, 1561),
    DIAMOND_PICKAXE(278, 1, 1561),
    DIAMOND_AXE(279, 1, 1561),
    STICK(280),
    BOWL(281),
    MUSHROOM_SOUP(282, 1, MaterialCategory.EDIBLE),
    GOLD_SWORD(283, 1, 32, MaterialCategory.SWORD),
    GOLD_SPADE(284, 1, 32),
    GOLD_PICKAXE(285, 1, 32),
    GOLD_AXE(286, 1, 32),
    STRING(287),
    FEATHER(288),
    SULPHUR(289),
    WOOD_HOE(290, 1, 59),
    STONE_HOE(291, 1, 131),
    IRON_HOE(292, 1, 250),
    DIAMOND_HOE(293, 1, 1561),
    GOLD_HOE(294, 1, 32),
    SEEDS(295),
    WHEAT(296),
    BREAD(297, MaterialCategory.EDIBLE),
    LEATHER_HELMET(298, 1, 55, MaterialCategory.ARMOR),
    LEATHER_CHESTPLATE(299, 1, 80, MaterialCategory.ARMOR),
    LEATHER_LEGGINGS(300, 1, 75, MaterialCategory.ARMOR),
    LEATHER_BOOTS(301, 1, 65, MaterialCategory.ARMOR),
    CHAINMAIL_HELMET(302, 1, 165, MaterialCategory.ARMOR),
    CHAINMAIL_CHESTPLATE(303, 1, 240, MaterialCategory.ARMOR),
    CHAINMAIL_LEGGINGS(304, 1, 225, MaterialCategory.ARMOR),
    CHAINMAIL_BOOTS(305, 1, 195, MaterialCategory.ARMOR),
    IRON_HELMET(306, 1, 165, MaterialCategory.ARMOR),
    IRON_CHESTPLATE(307, 1, 240, MaterialCategory.ARMOR),
    IRON_LEGGINGS(308, 1, 225, MaterialCategory.ARMOR),
    IRON_BOOTS(309, 1, 195, MaterialCategory.ARMOR),
    DIAMOND_HELMET(310, 1, 363, MaterialCategory.ARMOR),
    DIAMOND_CHESTPLATE(311, 1, 528, MaterialCategory.ARMOR),
    DIAMOND_LEGGINGS(312, 1, 495, MaterialCategory.ARMOR),
    DIAMOND_BOOTS(313, 1, 429, MaterialCategory.ARMOR),
    GOLD_HELMET(314, 1, 77, MaterialCategory.ARMOR),
    GOLD_CHESTPLATE(315, 1, 112, MaterialCategory.ARMOR),
    GOLD_LEGGINGS(316, 1, 105, MaterialCategory.ARMOR),
    GOLD_BOOTS(317, 1, 91, MaterialCategory.ARMOR),
    FLINT(318),
    PORK(319, MaterialCategory.EDIBLE),
    GRILLED_PORK(320, MaterialCategory.EDIBLE),
    PAINTING(321),
    GOLDEN_APPLE(322, MaterialCategory.EDIBLE),
    SIGN(323, 16),
    WOOD_DOOR(324, 64),
    BUCKET(325, 16),
    WATER_BUCKET(326, 1),
    LAVA_BUCKET(327, 1),
    MINECART(328, 1),
    SADDLE(329, 1),
    IRON_DOOR(330, 64),
    REDSTONE(331),
    SNOW_BALL(332, 16),
    BOAT(333, 1),
    LEATHER(334),
    MILK_BUCKET(335, 1),
    CLAY_BRICK(336),
    CLAY_BALL(337),
    SUGAR_CANE(338),
    PAPER(339),
    BOOK(340),
    SLIME_BALL(341),
    STORAGE_MINECART(342, 1),
    POWERED_MINECART(343, 1),
    EGG(344, 16),
    COMPASS(345),
    FISHING_ROD(346, 1, 64),
    WATCH(347),
    GLOWSTONE_DUST(348),
    RAW_FISH(349, MaterialCategory.EDIBLE),
    COOKED_FISH(350, MaterialCategory.EDIBLE),
    INK_SACK(351),
    BONE(352),
    SUGAR(353),
    CAKE(354, 1),
    BED(355, 1),
    DIODE(356),
    COOKIE(357, MaterialCategory.EDIBLE),
    MAP(358),
    SHEARS(359, 1, 238),
    MELON(360, MaterialCategory.EDIBLE),
    PUMPKIN_SEEDS(361),
    MELON_SEEDS(362),
    RAW_BEEF(363, MaterialCategory.EDIBLE),
    COOKED_BEEF(364, MaterialCategory.EDIBLE),
    RAW_CHICKEN(365, MaterialCategory.EDIBLE),
    COOKED_CHICKEN(366, MaterialCategory.EDIBLE),
    ROTTEN_FLESH(367, MaterialCategory.EDIBLE),
    ENDER_PEARL(368, 16),
    BLAZE_ROD(369),
    GHAST_TEAR(370),
    GOLD_NUGGET(371),
    NETHER_STALK(372),
    POTION(373),
    GLASS_BOTTLE(374),
    SPIDER_EYE(375, MaterialCategory.EDIBLE),
    FERMENTED_SPIDER_EYE(376),
    BLAZE_POWDER(377),
    MAGMA_CREAM(378),
    BREWING_STAND_ITEM(379),
    CAULDRON_ITEM(380),
    EYE_OF_ENDER(381),
    SPECKLED_MELON(382),
    MONSTER_EGG(383),
    EXP_BOTTLE(384, 64),
    FIREBALL(385, 64),
    BOOK_AND_QUILL(386, 1),
    WRITTEN_BOOK(387, 16),
    EMERALD(388, 64),
    ITEM_FRAME(389),
    FLOWER_POT_ITEM(390),
    CARROT_ITEM(391, MaterialCategory.EDIBLE),
    POTATO_ITEM(392, MaterialCategory.EDIBLE),
    BAKED_POTATO(393, MaterialCategory.EDIBLE),
    POISONOUS_POTATO(394, MaterialCategory.EDIBLE),
    EMPTY_MAP(395),
    GOLDEN_CARROT(396, MaterialCategory.EDIBLE),
    SKULL_ITEM(397),
    CARROT_STICK(398, 1, 25),
    NETHER_STAR(399),
    PUMPKIN_PIE(400, MaterialCategory.EDIBLE),
    FIREWORK(401),
    FIREWORK_CHARGE(402),
    ENCHANTED_BOOK(403, 1),
    REDSTONE_COMPARATOR(404),
    NETHER_BRICK_ITEM(405),
    QUARTZ(406),
    EXPLOSIVE_MINECART(407, 1),
    HOPPER_MINECART(408, 1),
    PRISMARINE_SHARD(409),
    PRISMARINE_CRYSTALS(410),
    RABBIT(411, MaterialCategory.EDIBLE),
    COOKED_RABBIT(412, MaterialCategory.EDIBLE),
    RABBIT_STEW(413, 1, MaterialCategory.EDIBLE),
    RABBIT_FOOT(414),
    RABBIT_HIDE(415),
    ARMOR_STAND(416, 16),
    IRON_BARDING(417, 1),
    GOLD_BARDING(418, 1),
    DIAMOND_BARDING(419, 1),
    LEASH(420),
    NAME_TAG(421),
    COMMAND_MINECART(422, 1),
    MUTTON(423, MaterialCategory.EDIBLE),
    COOKED_MUTTON(424, MaterialCategory.EDIBLE),
    BANNER(425, 16),
    SPRUCE_DOOR_ITEM(427),
    BIRCH_DOOR_ITEM(428),
    JUNGLE_DOOR_ITEM(429),
    ACACIA_DOOR_ITEM(430),
    DARK_OAK_DOOR_ITEM(431),
    GOLD_RECORD(2256, 1),
    GREEN_RECORD(2257, 1),
    RECORD_3(2258, 1),
    RECORD_4(2259, 1),
    RECORD_5(2260, 1),
    RECORD_6(2261, 1),
    RECORD_7(2262, 1),
    RECORD_8(2263, 1),
    RECORD_9(2264, 1),
    RECORD_10(2265, 1),
    RECORD_11(2266, 1),
    RECORD_12(2267, 1),
    ;

    private static final Map<String, Material> BY_NAME = Maps.newHashMap();
    private static Material[] byId = new Material[383];

    static {
        for (Material material : values()) {
            if (byId.length <= material.id) {
                byId = Arrays.copyOfRange(byId, 0, material.id + 2);
            }
            byId[material.id] = material;

            BY_NAME.put(material.name(), material);
        }
    }

    private final int id;
    private final int maxStack;
    private final short durability;

    private final MaterialCategory[] categories;

    Material(final int id, MaterialCategory... categories) {
        this(id, 64, categories);
    }

    Material(final int id, final int stack, MaterialCategory... categories) {
        this(id, stack, 0, categories);
    }

    Material(final int id, final int stack, final int durability, MaterialCategory... categories) {
        this.id = id;
        this.durability = (short) durability;
        this.maxStack = stack;
        this.categories = categories;
    }

    public static Material getMaterial(final int id) {
        if (byId.length > id && id >= 0) {
            return byId[id];
        } else {
            return null;
        }
    }

    public static Material getMaterial(final String name) {
        return BY_NAME.get(name);
    }

    public static Material matchMaterial(final String name) {
        Validate.notNull(name, "Name cannot be null");

        Material result = null;

        try {
            result = getMaterial(Integer.parseInt(name));
        } catch (NumberFormatException ignored) {
        }

        if (result == null) {
            String filtered = name.toUpperCase();

            filtered = filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
            result = BY_NAME.get(filtered);
        }

        return result;
    }

    public int getId() {
        return id;
    }

    public int getMaxStackSize() {
        return maxStack;
    }

    public short getMaxDurability() {
        return durability;
    }

    public MaterialCategory[] getCategories() {
        return this.categories;
    }

    public boolean hasCategory(MaterialCategory category) {
        for (MaterialCategory providedCategory : this.categories) {
            if (category == providedCategory) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlock() {
        return id < 256;
    }

    public boolean isEdible() {
        return this.hasCategory(MaterialCategory.EDIBLE);
    }

    public boolean isRecord() {
        return id >= GOLD_RECORD.id && id <= RECORD_12.id;
    }

    public boolean isSolid() {
        return this.hasCategory(MaterialCategory.SOLID);
    }

    public boolean isTransparent() {
        return this.hasCategory(MaterialCategory.TRANSPARENT);
    }

    public boolean isFlammable() {
        return this.hasCategory(MaterialCategory.FLAMMABLE);
    }

    public boolean isBurnable() {
        return this.hasCategory(MaterialCategory.BURNABLE);
    }

    public boolean isOccluding() {
        return this.hasCategory(MaterialCategory.OCCLUDING);
    }

    public boolean hasGravity() {
        return this.hasCategory(MaterialCategory.GRAVITY);
    }

    public boolean isDoor() {
        return this.hasCategory(MaterialCategory.DOOR);
    }

    public boolean isWooden() {
        return this.hasCategory(MaterialCategory.WOODEN);
    }

    public boolean isFence() {
        return this.hasCategory(MaterialCategory.FENCE);
    }

    public boolean isFenceGate() {
        return this.hasCategory(MaterialCategory.FENCE_GATE);
    }

    public boolean isSword() {
        return this.hasCategory(MaterialCategory.SWORD);
    }

    public boolean isArmor() {
        return this.hasCategory(MaterialCategory.ARMOR);
    }

}
