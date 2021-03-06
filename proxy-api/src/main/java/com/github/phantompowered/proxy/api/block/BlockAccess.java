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
package com.github.phantompowered.proxy.api.block;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.location.Location;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface BlockAccess {

    void trackBlockUpdates(UUID trackerId, int[] states, BlockConsumer consumer);

    void trackBlockUpdates(UUID trackerId, Material material, BlockConsumer consumer);

    void untrackBlockUpdates(UUID trackerId);

    @NotNull
    Collection<Location> getPositions(int state);

    @NotNull
    Collection<Location> getPositions(int[] states);

    @NotNull
    Collection<Location> getPositions(Material material);

    int getBlockState(@NotNull Location pos);

    @NotNull
    Material getMaterial(@NotNull Location pos);

    boolean isAirBlock(@NotNull Location pos);

    boolean isWaterBlock(@NotNull Location pos);

    boolean canSeeSky(@NotNull Location pos);

    void setMaterial(@NotNull Location pos, @Nullable Material material);

    void setBlockState(@NotNull Location pos, int blockState);

    BlockStateRegistry getBlockStateRegistry();

    int getDimension();

    Component[] getSignLines(@NotNull Location pos);

}
