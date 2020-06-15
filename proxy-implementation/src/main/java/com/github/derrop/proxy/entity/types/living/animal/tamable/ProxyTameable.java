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
package com.github.derrop.proxy.entity.types.living.animal.tamable;

import com.github.derrop.proxy.api.entity.LivingEntityType;
import com.github.derrop.proxy.api.entity.types.living.animal.tamable.Tameable;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.entity.types.living.animal.ageable.ProxyAgeable;

import java.util.UUID;

public class ProxyTameable extends ProxyAgeable implements Tameable {

    protected ProxyTameable(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, LivingEntityType type) {
        super(registry, client, spawnPacket, type);
    }

    @Override
    public boolean isSitting() {
        return (this.objectList.getByte(16) & 1) != 0;
    }

    @Override
    public boolean isTame() {
        return (this.objectList.getByte(16) & 4) != 0;
    }

    @Override
    public boolean hasOwner() {
        return this.getOwnerUniqueId() != null;
    }

    @Override
    public UUID getOwnerUniqueId() {
        try {
            return UUID.fromString(this.objectList.getString(17));
        } catch (Throwable throwable) {
            return null;
        }
    }
}
