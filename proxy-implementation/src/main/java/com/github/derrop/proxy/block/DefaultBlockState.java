package com.github.derrop.proxy.block;

import com.github.derrop.proxy.api.block.*;
import com.github.derrop.proxy.api.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultBlockState implements BlockState {

    private int id;
    private Material material;
    private SubMaterial subMaterial;
    private boolean open;
    private boolean powered;
    private int layers;
    private double height;
    private double thickness;
    private EnumFacing facing;
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

    DefaultBlockState facing(EnumFacing facing) {
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
    public EnumFacing getFacing() {
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
}
