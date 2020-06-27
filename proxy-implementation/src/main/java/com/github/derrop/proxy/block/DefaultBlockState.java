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
import com.google.errorprone.annotations.CanIgnoreReturnValue;
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
    private Facing.Axis axis;
    private boolean decayable;
    private boolean checkDecay;
    private boolean triggered;
    private boolean occupied;
    private BlockShape shape;
    private boolean isShort;
    private PistonType pistonType;
    private int age;
    private int moisture;
    private Direction direction;
    private ComparatorMode comparatorMode;

    public DefaultBlockState(int id, Material material) {
        this.id = id;
        this.material = material;
        this.height = 1;
        this.thickness = 1;
    }

    @CanIgnoreReturnValue
    DefaultBlockState open() {
        this.open = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState powered() {
        this.powered = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState facing(Facing facing) {
        this.facing = facing;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState triggered() {
        this.triggered = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState hinge(HingePosition position) {
        this.hingePosition = position;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState half(TrapdoorPosition position) {
        this.half = position;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState subMaterial(SubMaterial subMaterial) {
        this.subMaterial = subMaterial;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState axis(Facing.Axis axis) {
        this.axis = axis;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState decayable() {
        this.decayable = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState checkDecay() {
        this.checkDecay = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState occupied() {
        this.occupied = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState shape(BlockShape shape) {
        this.shape = shape;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState setShort() {
        this.isShort = true;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState pistonType(PistonType pistonType) {
        this.pistonType = pistonType;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState comparatorMode(ComparatorMode comparatorMode) {
        this.comparatorMode = comparatorMode;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState age(int age) {
        this.age = age;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState moisture(int moisture) {
        this.moisture = moisture;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState layers(int layers) {
        this.layers = layers;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState height(double height) {
        this.height = height;
        return this;
    }

    @CanIgnoreReturnValue
    DefaultBlockState thick(double thickness) {
        this.thickness = thickness;
        return this;
    }

    @CanIgnoreReturnValue
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
    public @Nullable Facing.Axis getAxis() {
        return this.axis;
    }

    @Override
    public boolean isDecayable() {
        return this.decayable;
    }

    @Override
    public boolean isCheckDecay() {
        return this.checkDecay;
    }

    @Override
    public boolean isTriggered() {
        return this.triggered;
    }

    @Override
    public boolean isOccupied() {
        return this.occupied;
    }

    @Override
    public @Nullable BlockShape getShape() {
        return this.shape;
    }

    @Override
    public boolean isShort() {
        return this.isShort;
    }

    @Override
    public PistonType getPistonType() {
        return this.pistonType;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public int getMoisture() {
        return this.moisture;
    }

    @Override
    public @Nullable Direction getDirection() {
        return this.direction;
    }

    @Override
    public @Nullable ComparatorMode getComparatorMode() {
        return this.comparatorMode;
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
