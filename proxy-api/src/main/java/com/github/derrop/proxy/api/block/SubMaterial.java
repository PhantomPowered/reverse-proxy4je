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

    COLOR_WHITE(0, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_ORANGE(1, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_MAGENTA(2, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_LIGHT_BLUE(3, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_YELLOW(4, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_LIME(5, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_PINK(6, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_GRAY(7, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_LIGHT_GRAY(8, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_CYAN(9, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_PURPLE(10, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_BLUE(11, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_BROWN(12, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_GREEN(13, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_RED(14, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE),
    COLOR_BLACK(15, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE);

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
