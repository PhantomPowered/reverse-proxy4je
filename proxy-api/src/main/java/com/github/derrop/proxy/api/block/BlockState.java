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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockState {

    int getId();

    @NotNull
    Material getMaterial();

    boolean isPowered();

    boolean isOpen();

    int getLayers();

    double getHeight();

    double getThickness();

    boolean isPassable();

    int getRedstonePower();

    @Nullable
    SubMaterial getSubMaterial();

    @Nullable
    HingePosition getHingePosition();

    @Nullable
    Facing getFacing();

    @Nullable
    Half getHalf();

    @Nullable
    Facing.Axis getAxis();

    boolean isDecayable();

    boolean isCheckDecay();

    boolean isTriggered();

    boolean isOccupied();

    @Nullable
    BlockShape getShape();

    boolean isShort();

    @Nullable
    PistonType getPistonType();

    int getAge();

    int getMoisture();

    int getRotation();

    @Nullable
    ComparatorMode getComparatorMode();

    int getDamage();

}
