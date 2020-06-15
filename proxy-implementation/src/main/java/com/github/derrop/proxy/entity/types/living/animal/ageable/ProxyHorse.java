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
package com.github.derrop.proxy.entity.types.living.animal.ageable;

import com.github.derrop.proxy.api.entity.LivingEntityType;
import com.github.derrop.proxy.api.entity.types.living.animal.ageable.Horse;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.entity.types.living.animal.ProxyAnimal;

import java.util.UUID;

public class ProxyHorse extends ProxyAnimal implements Horse {

    public ProxyHorse(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        super(registry, client, spawnPacket, LivingEntityType.HORSE);
        this.setSize(1.4F, 1.6F);
    }

    @Override
    public boolean isTame() {
        return (this.objectList.getInt(16) & 2) != 0;
    }

    @Override
    public boolean hasSaddle() {
        return (this.objectList.getInt(16) & 4) != 0;
    }

    @Override
    public boolean hasChest() {
        return (this.objectList.getInt(16) & 8) != 0;
    }

    @Override
    public boolean isBread() {
        return (this.objectList.getInt(16) & 10) != 0;
    }

    @Override
    public boolean isEating() {
        return (this.objectList.getInt(16) & 20) != 0;
    }

    @Override
    public boolean isRearing() {
        return (this.objectList.getInt(16) & 40) != 0;
    }

    @Override
    public boolean hasMouthOpen() {
        return (this.objectList.getInt(16) & 80) != 0;
    }

    @Override
    public Type getHorseType() {
        return Type.values()[super.objectList.getByte(19)];
    }

    @Override
    public Color getColor() {
        return Color.values()[this.objectList.getInt(20) & 255];
    }

    @Override
    public Style getStyle() {
        return Style.values()[this.objectList.getInt(20) >>> 8];
    }

    @Override
    public boolean hasOwner() {
        return this.getOwnerUniqueId() != null;
    }

    @Override
    public UUID getOwnerUniqueId() {
        try {
            return UUID.fromString(this.objectList.getString(21));
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Override
    public Armor getArmor() {
        return Armor.values()[this.objectList.getInt(22)];
    }

    @Override
    public float getHeadHeight() {
        return this.length;
    }
}
