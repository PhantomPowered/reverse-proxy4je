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
package com.github.phantompowered.proxy.connection;

import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.connection.InteractiveServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.item.ProxyItemStack;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientArmAnimation;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientBlockPlace;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientPlayerDigging;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientEntityAction;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientUseEntity;
import org.jetbrains.annotations.NotNull;

public class BasicInteractiveServiceConnection implements InteractiveServiceConnection {

    private static final Location AIR_LOCATION = new Location(-1, -1, -1, 0, 0, false);

    private final BasicServiceConnection connection;

    public BasicInteractiveServiceConnection(BasicServiceConnection connection) {
        this.connection = connection;
    }

    @Override
    public ServiceConnection basic() {
        return this.connection;
    }

    @Override
    public void teleport(@NotNull Location location) {
        this.connection.setLocation(location);
    }

    @Override
    public void breakBlock(Location blockLocation, Facing facing) {
        if (!this.connection.getBlockAccess().getMaterial(blockLocation).isSolid()) {
            return;
        }

        this.connection.sendPacket(new PacketPlayClientPlayerDigging(blockLocation, facing, PacketPlayClientPlayerDigging.Action.START_DESTROY_BLOCK));
        this.connection.sendPacket(new PacketPlayClientPlayerDigging(blockLocation, facing, PacketPlayClientPlayerDigging.Action.STOP_DESTROY_BLOCK));
    }

    @Override
    public void performAirLeftClick() {
        this.connection.sendPacket(new PacketPlayClientArmAnimation());
    }

    private void useEntity(int entityId, PacketPlayClientUseEntity.Action action, Vector hitVector) {
        this.connection.sendPacket(new PacketPlayClientUseEntity(entityId, action, hitVector));
    }

    @Override
    public void performEntityLeftClick(@NotNull Entity entity) {
        this.useEntity(entity.getEntityId(), PacketPlayClientUseEntity.Action.ATTACK, null);
    }

    @Override
    public void performBlockLeftClick(@NotNull Location blockLocation, @NotNull Facing facing) {
        this.connection.sendPacket(new PacketPlayClientPlayerDigging(blockLocation, facing, PacketPlayClientPlayerDigging.Action.START_DESTROY_BLOCK));
        this.connection.sendPacket(new PacketPlayClientPlayerDigging(blockLocation, facing, PacketPlayClientPlayerDigging.Action.ABORT_DESTROY_BLOCK));
    }

    @Override
    public void performAirRightClick() {
        ItemStack item = this.connection.getInventory().getItemInHand();
        if (item == null) {
            return;
        }
        this.connection.sendPacket(new PacketPlayClientBlockPlace(AIR_LOCATION, 255, item, 0, 0, 0));
    }

    @Override
    public void performEntityRightClick(@NotNull Entity entity, @NotNull Vector hitVector) {
        this.useEntity(entity.getEntityId(), PacketPlayClientUseEntity.Action.INTERACT, null);
        this.useEntity(entity.getEntityId(), PacketPlayClientUseEntity.Action.INTERACT_AT, hitVector);
    }

    @Override
    public void performBlockRightClick(@NotNull Location blockLocation, @NotNull Facing facing, @NotNull Vector vector) {
        ItemStack item = this.connection.getInventory().getItemInHand();
        if (item == null) {
            item = ProxyItemStack.AIR;
        }
        this.connection.sendPacket(new PacketPlayClientBlockPlace(blockLocation, facing.getIndex(), item, (float) vector.getX(), (float) vector.getY(), (float) vector.getZ()));
    }

    @Override
    public void toggleSneaking(boolean sneaking) {
        if (this.connection.isSneaking() != sneaking) {
            this.connection.sendPacket(new PacketPlayClientEntityAction(this.connection.getEntityId(), sneaking ? PacketPlayClientEntityAction.Action.START_SNEAKING : PacketPlayClientEntityAction.Action.STOP_SNEAKING));
        }
    }

    @Override
    public void toggleSprinting(boolean sprinting) {
        if (this.connection.isSprinting() != sprinting) {
            this.connection.sendPacket(new PacketPlayClientEntityAction(this.connection.getEntityId(), sprinting ? PacketPlayClientEntityAction.Action.START_SPRINTING : PacketPlayClientEntityAction.Action.STOP_SPRINTING));
        }
    }

    @Override
    public void openInventory() {
        this.connection.sendPacket(new PacketPlayClientEntityAction(this.connection.getEntityId(), PacketPlayClientEntityAction.Action.OPEN_INVENTORY));
    }
}
