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
package com.github.derrop.proxy.entity.types.living.monster;

import com.github.derrop.proxy.api.entity.LivingEntityType;
import com.github.derrop.proxy.api.entity.types.living.monster.Zombie;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import org.jetbrains.annotations.NotNull;

public class ProxyZombie extends ProxyMonster implements Zombie {

    public ProxyZombie(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, LivingEntityType type) {
        super(registry, client, spawnPacket, type);
        this.setSize(0.6F, 1.95F);
    }

    @Override
    public boolean isChild() {
        return this.objectList.getByte(12) > 0;
    }

    @Override
    public boolean isVillager() {
        return this.objectList.getByte(13) > 0;
    }

    @Override
    public boolean isConverting() {
        return this.objectList.getByte(14) > 0;
    }

    @Override
    public float getHeadHeight() {
        float f = 1.74F;

        if (this.isChild()) {
            f = (float) ((double) f - 0.81D);
        }

        return f;
    }

    @Override
    public void handleEntityPacket(@NotNull Packet packet) {
        super.handleEntityPacket(packet);

        if (packet instanceof PacketPlayServerEntityMetadata) {
            this.setSize(this.isChild() ? 0.6F * 0.5F : 0.6F, this.isChild() ? 1.95F * 0.5F : 1.95F);
        }
    }
}
