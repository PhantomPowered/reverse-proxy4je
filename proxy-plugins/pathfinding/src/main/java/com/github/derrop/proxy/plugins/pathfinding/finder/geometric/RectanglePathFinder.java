package com.github.derrop.proxy.plugins.pathfinding.finder.geometric;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.PathPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

public class RectanglePathFinder extends GeometricPathFinder {

    public Queue<PathPoint> findRectanglePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos pos1, @NotNull BlockPos pos2) {
        int lowerX = Math.min(pos1.getX(), pos2.getX());
        int upperX = Math.max(pos1.getX(), pos2.getX());

        int y = pos1.getY();

        int lowerZ = Math.min(pos1.getZ(), pos2.getZ());
        int upperZ = Math.max(pos1.getZ(), pos2.getZ());

        Collection<BlockPos> result = new ArrayList<>();

        for (int x = lowerX; x < upperX; x++) {
            result.add(new BlockPos(x, y, lowerZ));
            result.add(new BlockPos(x, y, upperZ));
        }

        for (int z = lowerZ; z < upperZ; z++) {
            result.add(new BlockPos(lowerX, y, z));
            result.add(new BlockPos(upperX, y, z));
        }

        System.out.println(result);
        for (BlockPos blockPos : result) {
            access.setMaterial(blockPos, Material.EMERALD_BLOCK);
            try {
                Thread.sleep(20);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        return super.sortByStartPoint(result, start);
    }

}
