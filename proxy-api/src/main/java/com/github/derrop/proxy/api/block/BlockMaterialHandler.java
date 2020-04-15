package com.github.derrop.proxy.api.block;

import org.jetbrains.annotations.Nullable;

public abstract class BlockMaterialHandler implements BlockConsumer {

    private final BlockStateRegistry registry;

    public BlockMaterialHandler(BlockStateRegistry registry) {
        this.registry = registry;
    }

    // materials will be null if the chunk was unloaded (newMaterial is null) or loaded (oldMaterial is null)
    public abstract void accept(int x, int y, int z, @Nullable Material oldMaterial, @Nullable Material newMaterial);

    @Override
    public void accept(int x, int y, int z, int oldState, int state) {
        this.accept(x, y, z, oldState == -1 ? null : this.registry.getMaterial(oldState), state == -1 ? null : this.registry.getMaterial(state));
    }
}
