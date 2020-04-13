package com.github.derrop.proxy.api.block;

import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.api.util.EnumFacing;

public interface BlockAccess {

    int getBlockState(BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(BlockPos pos);

    boolean isWaterBlock(BlockPos pos);

    default int getStrongPower(BlockPos pos, EnumFacing direction) { // redstone (0 - 15)
        return 0;
    }

}
