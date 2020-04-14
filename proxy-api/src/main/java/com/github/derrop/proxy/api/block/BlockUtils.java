package com.github.derrop.proxy.api.block;

import org.jetbrains.annotations.NotNull;

// TODO: @derrop move this to impl?
public final class BlockUtils {

    private BlockUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isPassable(@NotNull BlockAccess blockAccess, int blockState) {
        Material material = blockAccess.getMaterial(blockState);

        return material == Material.AIR ||
                material == Material.SIGN_POST || material == Material.WALL_SIGN ||
                material == Material.GOLD_PLATE || material == Material.IRON_PLATE || material == Material.STONE_PLATE || material == Material.WOOD_PLATE ||
                material == Material.STANDING_BANNER || material == Material.WALL_BANNER ||
                material == Material.WATER || material == Material.STATIONARY_WATER ||
                ((material == Material.TRAP_DOOR || material == Material.IRON_TRAPDOOR) /* && blockState.isOpen TODO */) ||
                (material.isDoor() /* && blockState.isOpen TODO */) ||
                (material.isFenceGate() /* && blockState.isOpen TODO */) ||
                (material == Material.SNOW /* && blockState.layers < 5 */);
    }
}
