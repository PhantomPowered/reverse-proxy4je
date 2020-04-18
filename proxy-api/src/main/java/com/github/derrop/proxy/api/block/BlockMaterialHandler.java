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
