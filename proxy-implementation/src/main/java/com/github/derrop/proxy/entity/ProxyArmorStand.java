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
package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.entity.EntityType;
import com.github.derrop.proxy.api.entity.types.ArmorStand;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.EulerAngle;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.entity.types.living.ProxyEntityLiving;
import com.github.derrop.proxy.item.ProxyItemStack;
import org.jetbrains.annotations.NotNull;

public class ProxyArmorStand extends ProxyEntityLiving implements ArmorStand {

    protected ProxyArmorStand(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        super(registry, client, spawnPacket, EntityType.ARMOR_STAND.getTypeId());
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull EquipmentSlot slot) {
        return this.equipment.getOrDefault(slot.getSlotId(), ProxyItemStack.AIR);
    }

    @Override
    public @NotNull EulerAngle getBodyPosition(@NotNull BodyPosition position) {
        switch (position) {
            case HEAD:
                return this.objectList.getRotations(11).asEuler();
            case BODY:
                return this.objectList.getRotations(12).asEuler();
            case ARM_LEFT:
                return this.objectList.getRotations(13).asEuler();
            case ARM_RIGHT:
                return this.objectList.getRotations(14).asEuler();
            case LEG_LEFT:
                return this.objectList.getRotations(15).asEuler();
            case LEG_RIGHT:
                return this.objectList.getRotations(16).asEuler();
        }

        throw new RuntimeException("Magic happened");
    }

    @Override
    public boolean hasBasePlate() {
        return (this.objectList.getByte(10) & 8) != 0;
    }

    @Override
    public boolean hasGravity() {
        return (this.objectList.getByte(10) & 2) != 0;
    }

    @Override
    public boolean hasArms() {
        return (this.objectList.getByte(10) & 4) != 0;
    }

    @Override
    public boolean isSmall() {
        return (this.objectList.getByte(10) & 1) != 0;
    }

    @Override
    public boolean isMarker() {
        return (this.objectList.getByte(10) & 16) != 0;
    }

    @Override
    public float getHeadHeight() {
        return this.isSmall() ? this.length * 0.5F : this.length * 0.9F;
    }
}
