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

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniMapCache implements PacketCacheHandler {

    private Map<Integer, PacketPlayServerMap> maps = new ConcurrentHashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.MAP};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        PacketPlayServerMap map = (PacketPlayServerMap) newPacket;
        if (!this.maps.containsKey(map.getMapId())) {
            this.maps.put(map.getMapId(), map);
            return;
        }

        if (this.maps.containsKey(map.getMapId())) {
            PacketPlayServerMap oldMap = this.maps.get(map.getMapId());
            oldMap.setVisiblePlayers(map.getVisiblePlayers());
            oldMap.setMapScale(map.getMapScale());
            if (map.getMapMaxX() > 0) {
                oldMap.setMapDataBytes(new byte[map.getMapDataBytes().length]);
                System.arraycopy(map.getMapDataBytes(), 0, oldMap.getMapDataBytes(), 0, map.getMapDataBytes().length);
            }
        } else {
            this.maps.put(map.getMapId(), map);
        }
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        for (PacketPlayServerMap map : this.maps.values()) {
            con.sendPacket(map);
        }
    }

    // TODO the maps aren't always correctly displayed

}
