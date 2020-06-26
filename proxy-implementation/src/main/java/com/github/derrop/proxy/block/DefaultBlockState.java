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
package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.block.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultBlockState implements BlockState {

    private final int id;
    private final Material material;
    private SubMaterial subMaterial;
    private boolean open;
    private boolean powered;
    private int layers;
    private double height;
    private double thickness;
    private int redstonePower;
    private Facing facing;
    private HingePosition hingePosition;
    private TrapdoorPosition half;

    public DefaultBlockState(int id, Material material) {
        this.id = id;
        this.material = material;
        this.height = 1;
        this.thickness = 1;
    }

    DefaultBlockState open() {
        this.open = true;
        return this;
    }

    DefaultBlockState powered() {
        this.powered = true;
        return this;
    }

    DefaultBlockState facing(Facing facing) {
        this.facing = facing;
        return this;
    }

    DefaultBlockState hinge(HingePosition position) {
        this.hingePosition = position;
        return this;
    }

    DefaultBlockState half(TrapdoorPosition position) {
        this.half = position;
        return this;
    }

    DefaultBlockState subMaterial(SubMaterial subMaterial) {
        this.subMaterial = subMaterial;
        return this;
    }

    DefaultBlockState layers(int layers) {
        this.layers = layers;
        return this;
    }

    DefaultBlockState height(double height) {
        this.height = height;
        return this;
    }

    DefaultBlockState thick(double thickness) {
        this.thickness = thickness;
        return this;
    }

    DefaultBlockState power(int redstonePower) {
        this.redstonePower = redstonePower;
        return this;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @NotNull
    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public int getLayers() {
        return this.layers;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getThickness() {
        return this.thickness;
    }

    @Override
    public @Nullable SubMaterial getSubMaterial() {
        return this.subMaterial;
    }

    @Override
    public boolean isPowered() {
        return this.powered;
    }

    @Override
    public Facing getFacing() {
        return this.facing;
    }

    @Override
    public @Nullable TrapdoorPosition getHalf() {
        return this.half;
    }

    @Override
    public HingePosition getHingePosition() {
        return this.hingePosition;
    }

    @Override
    public boolean isPassable() {
        return this.material == Material.AIR ||
                this.material == Material.SIGN_POST || this.material == Material.WALL_SIGN ||
                this.material == Material.GOLD_PLATE || this.material == Material.IRON_PLATE || this.material == Material.STONE_PLATE || this.material == Material.WOOD_PLATE ||
                this.material == Material.STANDING_BANNER || this.material == Material.WALL_BANNER ||
                this.material == Material.WATER || this.material == Material.STATIONARY_WATER ||
                ((this.material == Material.TRAP_DOOR || this.material == Material.IRON_TRAPDOOR) && this.isOpen()) ||
                (this.material.isDoor() && this.isOpen()) ||
                (this.material.isFenceGate() && this.isOpen()) ||
                (this.material == Material.SNOW && this.getLayers() < 5);
    }

    @Override
    public int getRedstonePower() {
        return this.redstonePower;
    }
}
