package com.github.derrop.proxy.plugins.betterlogin.connection;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockConsumer;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.util.Vector;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.plugins.betterlogin.LoginPrepareListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LoginBlockAccess implements BlockAccess {

    private static final Material MATERIAL = Material.DIRT;

    private final Collection<Location> filledBlocks = new ArrayList<>();

    private final int state;
    private final BlockStateRegistry registry;

    public LoginBlockAccess(DefaultPlayer player) {
        this.registry = player.getProxy().getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class);
        this.state = this.registry.getDefaultBlockState(MATERIAL);

        Location origin = LoginPrepareListener.SPAWN;
        Location lower = origin.subtract(new Vector(5, 3, 5));
        Location upper = origin.add(new Vector(5, -3, 5));

        player.sendBlockChange(origin.down(2), Material.DIRT);
        for (int x = lower.getBlockX(); x < upper.getBlockX(); x++) {
            for (int z = lower.getBlockZ(); z < upper.getBlockZ(); z++) {
                Location pos = new Location(x, origin.getBlockY(), z);
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

    private boolean isFilled(Location pos) {
        return this.filledBlocks.contains(pos);
    }

    @Override
    public @NotNull Collection<Location> getPositions(int state) {
        return state == this.state ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public @NotNull Collection<Location> getPositions(int[] states) {
        return Arrays.stream(states).anyMatch(value -> value == this.state) ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public @NotNull Collection<Location> getPositions(Material material) {
        return material == MATERIAL ? this.filledBlocks : Collections.emptyList();
    }

    @Override
    public int getBlockState(@NotNull Location pos) {
        return this.isFilled(pos) ? this.state : 0;
    }

    @Override
    public @NotNull Material getMaterial(@NotNull Location pos) {
        return this.isFilled(pos) ? MATERIAL : Material.AIR;
    }

    @Override
    public boolean isAirBlock(@NotNull Location pos) {
        return this.getMaterial(pos) == Material.AIR;
    }

    @Override
    public boolean isWaterBlock(@NotNull Location pos) {
        return false;
    }

    @Override
    public boolean canSeeSky(@NotNull Location pos) {
        return true;
    }

    @Override
    public void setMaterial(@NotNull Location pos, @Nullable Material material) {
    }

    @Override
    public void setBlockState(@NotNull Location pos, int blockState) {
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
