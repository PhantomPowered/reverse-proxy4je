package com.github.derrop.proxy.api.block;

public enum SubMaterial {

    GRANITE(Material.STONE, 1),
    POLISHED_GRANITE(Material.STONE, 2),
    DIORITE(Material.STONE, 3),
    POLISHED_DIORITE(Material.STONE, 4),
    ANDESITE(Material.STONE, 5),
    POLISHED_ANDESITE(Material.STONE, 6),

    COARSE_DIRT(Material.DIRT, 1),
    PODZOL(Material.DIRT, 2);

    private Material parent;
    private int subId;

    SubMaterial(Material parent, int subId) {
        this.parent = parent;
        this.subId = subId;
    }
}
