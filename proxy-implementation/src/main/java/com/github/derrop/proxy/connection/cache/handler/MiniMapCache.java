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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerMap;
import com.github.derrop.proxy.api.network.PacketSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniMapCache implements PacketCacheHandler {

    private Map<Integer, PacketPlayServerMap> maps = new ConcurrentHashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.MAPS};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        PacketPlayServerMap maps = (PacketPlayServerMap) newPacket.getDeserializedPacket();
        if (!this.maps.containsKey(maps.getMapId())) {
            this.maps.put(maps.getMapId(), maps);
            return;
        }

        if (this.maps.containsKey(maps.getMapId())) {
            PacketPlayServerMap oldMaps = this.maps.get(maps.getMapId());
            oldMaps.setMapVisiblePlayersVec4b(maps.getMapVisiblePlayersVec4b());
            oldMaps.setMapScale(maps.getMapScale());
            if (maps.getMapMaxX() > 0) {
                for (int j = 0; j < maps.getMapMaxX(); ++j) {
                    for (int k = 0; k < maps.getMapMaxY(); ++k) {
                        int i = j + k * maps.getMapMaxX();
                        oldMaps.getMapDataBytes()[i] = maps.getMapDataBytes()[i];
                    }
                }
            }
        } else {
            this.maps.put(maps.getMapId(), maps);
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (PacketPlayServerMap maps : this.maps.values()) {
            con.sendPacket(maps);
        }
    }
}
