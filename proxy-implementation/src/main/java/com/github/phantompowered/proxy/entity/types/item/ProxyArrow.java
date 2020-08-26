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
package com.github.phantompowered.proxy.entity.types.item;

import com.github.phantompowered.proxy.api.entity.EntityType;
import com.github.phantompowered.proxy.api.entity.types.item.Arrow;
import com.github.phantompowered.proxy.api.network.util.PositionedPacket;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;

public class ProxyArrow extends ProxyProjectile implements Arrow {

    private final int tracked;

    public ProxyArrow(ServiceRegistry registry, ConnectedProxyClient client, int tracked, PositionedPacket spawnPacket) {
        super(registry, client, spawnPacket, EntityType.ARROW);
        this.setSize(0.5F, 0.5F);
        this.tracked = tracked;
    }

    @Override
    public boolean isCritical() {
        return this.objectList.getByte(16) > 0;
    }

    @Override
    public int getShooter() {
        return this.tracked;
    }

    @Override
    public float getHeadHeight() {
        return 0.0F;
    }
}
