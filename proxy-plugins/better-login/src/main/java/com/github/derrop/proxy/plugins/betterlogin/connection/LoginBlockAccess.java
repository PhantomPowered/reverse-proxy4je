package com.github.derrop.proxy.plugins.betterlogin.connection;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.util.Vec3i;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.plugins.betterlogin.LoginPrepareListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LoginBlockAccess implements BlockAccess {

    private static final Material MATERIAL = Material.DIRT;

    private final Collection<BlockPos> filledBlocks = new ArrayList<>();

    private final int state;
    private final BlockStateRegistry registry;

    public LoginBlockAccess(DefaultPlayer player) {
        this.registry = player.getProxy().getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class);
        this.state = this.registry.getDefaultBlockState(MATERIAL);

        BlockPos origin = LoginPrepareListener.SPAWN.toBlockPos();
        BlockPos lower = origin.subtract(new Vec3i(5, 3, 5));
        BlockPos upper = origin.add(new Vec3i(5, -3, 5));

        player.sendBlockChange(origin.down(2), Material.DIRT);
        for (int x = lower.getX(); x < upper.getX(); x++) {
            for (int z = lower.getZ(); z < upper.getZ(); z++) {
                BlockPos pos = new BlockPos(x, origin.getY(), z);
                player.sendBlockChange(pos, MATERIAL);
                this.filledBlocks.add(pos);
            }
        }
    }


    @Override
    public void trackBlockUpdates(UUID trackerId, int[] states, BlockConsumer consumer) {
    }

    @Override
    public void trackBlockUpdates(UUID trackerId, Material material, BlockConsumer consumer) {
    }

    @Override
    public void untrackBlockUpdates(UUID trackerId) {
    }

    private boolean isFilled(BlockPos pos) {
        return this.filledBlocks.contains(pos);
    }

    @Override
    public @NotNull Collection<BlockPos> getPositions(int state) {
        return state == this.state ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public @NotNull Collection<BlockPos> getPositions(int[] states) {
        return Arrays.stream(states).anyMatch(value -> value == this.state) ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public @NotNull Collection<BlockPos> getPositions(Material material) {
        return material == MATERIAL ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public int getBlockState(@NotNull BlockPos pos) {
        return this.isFilled(pos) ? this.state : 0;
    }

    @Override
    public @NotNull Material getMaterial(@NotNull BlockPos pos) {
        return this.isFilled(pos) ? MATERIAL : Material.AIR;
    }

    @Override
    public boolean isAirBlock(@NotNull BlockPos pos) {
        return this.getMaterial(pos) == Material.AIR;
    }

    @Override
    public boolean isWaterBlock(@NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean canSeeSky(@NotNull BlockPos pos) {
        return true;
    }

    @Override
    public void setMaterial(@NotNull BlockPos pos, @Nullable Material material) {
    }

    @Override
    public void setBlockState(@NotNull BlockPos pos, int blockState) {
    }

    @Override
    public BlockStateRegistry getBlockStateRegistry() {
        return this.registry;
    }

    @Override
    public int getDimension() {
        return 0;
    }
}
