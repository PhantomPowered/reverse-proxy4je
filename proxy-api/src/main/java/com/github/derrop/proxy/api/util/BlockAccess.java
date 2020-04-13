package com.github.derrop.proxy.api.util;

public interface BlockAccess {

    int getCombinedLight(BlockPos pos, int lightValue);

    int getBlockState(BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(BlockPos pos);

    boolean isWaterBlock(BlockPos pos);

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    boolean extendedLevelsInChunkCache();

    int getStrongPower(BlockPos pos, EnumFacing direction);

}
