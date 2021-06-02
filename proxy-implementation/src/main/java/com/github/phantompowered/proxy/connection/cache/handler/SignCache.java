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
package com.github.phantompowered.proxy.connection.cache.handler;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.PacketCacheHandler;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.world.PacketPlayServerUpdateSign;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SignCache implements PacketCacheHandler {

    private final Map<Location, PacketPlayServerUpdateSign> signUpdates = new ConcurrentHashMap<>();

    private PacketCache packetCache;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.UPDATE_SIGN};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        this.packetCache = packetCache;

        PacketPlayServerUpdateSign sign = (PacketPlayServerUpdateSign) newPacket;
        this.signUpdates.put(sign.getLocation(), sign);
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        for (Map.Entry<Location, PacketPlayServerUpdateSign> entry : this.signUpdates.entrySet()) {
            Material material = this.packetCache.getMaterialAt(entry.getKey());

            if (material != Material.WALL_SIGN && material != Material.SIGN_POST) {
                this.signUpdates.remove(entry.getKey());
                continue;
            }

            con.sendPacket(entry.getValue());
        }
    }

    public Map<Location, PacketPlayServerUpdateSign> getSignUpdates() {
        return this.signUpdates;
    }
}
