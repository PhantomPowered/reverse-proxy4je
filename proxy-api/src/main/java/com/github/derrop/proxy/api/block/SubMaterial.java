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
package com.github.derrop.proxy.api.block;

public enum SubMaterial {

    GRANITE(Material.STONE, 1),
    POLISHED_GRANITE(Material.STONE, 2),
    DIORITE(Material.STONE, 3),
    POLISHED_DIORITE(Material.STONE, 4),
    ANDESITE(Material.STONE, 5),
    POLISHED_ANDESITE(Material.STONE, 6),

    COARSE_DIRT(Material.DIRT, 1),
    PODZOL(Material.DIRT, 2),

    COLOR_WHITE(0, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_ORANGE(1, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_MAGENTA(2, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_LIGHT_BLUE(3, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_YELLOW(4, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_LIME(5, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_PINK(6, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_GRAY(7, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_LIGHT_GRAY(8, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_CYAN(9, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_PURPLE(10, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_BLUE(11, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_BROWN(12, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_GREEN(13, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_RED(14, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),
    COLOR_BLACK(15, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET),

    WOOD_OAK(0, Material.PLANKS, Material.LOG, Material.SAPLING, Material.LEAVES),
    WOOD_SPRUCE(1, Material.PLANKS, Material.LOG, Material.SAPLING, Material.LEAVES),
    WOOD_BIRCH(2, Material.PLANKS, Material.LOG, Material.SAPLING, Material.LEAVES),
    WOOD_JUNGLE(3, Material.PLANKS, Material.LOG, Material.SAPLING, Material.LEAVES),
    WOOD_ACACIA(0, Material.PLANKS, Material.LOG_2, Material.SAPLING, Material.LEAVES_2),
    WOOD_DARK_OAK(1, Material.PLANKS, Material.LOG_2, Material.SAPLING, Material.LEAVES_2),

    SANDSTONE_NORMAL(0, Material.SANDSTONE, Material.RED_SANDSTONE),
    SANDSTONE_CHISELED(1, Material.SANDSTONE, Material.RED_SANDSTONE),
    SANDSTONE_SMOOTH(2, Material.SANDSTONE, Material.RED_SANDSTONE),

    PRISMARINE_NORMAL(0, Material.PRISMARINE),
    PRISMARINE_BRICKS(1, Material.PRISMARINE),
    PRISMARINE_DARK(2, Material.PRISMARINE),

    SAND_NORMAL(0, Material.SAND),
    SAND_RED(1, Material.SAND),

    SPONGE_NORMAL(0, Material.SPONGE),
    SPONGE_WET(1, Material.SPONGE),

    QUARTZ_NORMAL(0, Material.QUARTZ_BLOCK),
    QUARTZ_CHISELED(1, Material.QUARTZ_BLOCK),
    QUARTZ_PILLAR(2, Material.QUARTZ_BLOCK),

    PLANT_SUN_FLOWER(0, Material.DOUBLE_PLANT),
    PLANT_LILAC(1, Material.DOUBLE_PLANT),
    PLANT_TALL_GRASS(2, Material.DOUBLE_PLANT),
    PLANT_LARGE_FERN(3, Material.DOUBLE_PLANT),
    PLANT_RED_ROSE(4, Material.DOUBLE_PLANT),
    PLANT_PEONY(5, Material.DOUBLE_PLANT),

    STONE_SLAB_STONE(0, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_SANDSTONE(1, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_WOOD_OLD(2, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_COBBLESTONE(3, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_BRICKS(4, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_STONE_BRICKS(5, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_NETHER_BRICKS(6, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),
    STONE_SLAB_QUARTZ(7, Material.STONE_SLAB, Material.DOUBLE_STONE_SLAB),

    STONE_SLAB_RED_SANDSTONE(0, Material.STONE_SLAB2, Material.DOUBLE_STONE_SLAB2),

    FLOWER_POPPY(0, Material.FLOWER),
    FLOWER_BLUE_ORCHID(1, Material.FLOWER),
    FLOWER_ALLIUM(2, Material.FLOWER),
    FLOWER_AZURE_BLUET(3, Material.FLOWER),
    FLOWER_RED_TULIP(4, Material.FLOWER),
    FLOWER_ORANGE_TULIP(5, Material.FLOWER),
    FLOWER_WHITE_TULIP(6, Material.FLOWER),
    FLOWER_PINK_TULIP(7, Material.FLOWER),
    FLOWER_OXEYE_DAISY(8, Material.FLOWER),

    STONE(0, Material.MONSTER_EGGS),
    COBBLESTONE(1, Material.MONSTER_EGGS),
    STONE_BRICK_EGG(2, Material.MONSTER_EGGS),
    MOSSY_STONE_BRICK_EGG(3, Material.MONSTER_EGGS),
    CRACKED_STONE_BRICK_EGG(4, Material.MONSTER_EGGS),
    CHISELED_STONE_BRICK_EGG(5, Material.MONSTER_EGGS),

    STONE_BRICK(0, Material.SMOOTH_BRICK),
    MOSSY_STONE_BRICK(1, Material.SMOOTH_BRICK),
    CRACKED_STONE_BRICK(2, Material.SMOOTH_BRICK),
    CHISELED_STONE_BRICK(3, Material.SMOOTH_BRICK),

    COBBLESTONE_WALL(0, Material.COBBLE_WALL),
    MOSSY_COBBLESTONE_WALL(1, Material.COBBLE_WALL);

    private final Material[] parents;
    private final int subId;

    SubMaterial(Material parent, int subId) {
        this(subId, parent);
    }

    SubMaterial(int subId, Material... parents) {
        this.parents = parents;
        this.subId = subId;
    }

    public Material[] getParents() {
        return this.parents;
    }

    public int getSubId() {
        return this.subId;
    }
}
