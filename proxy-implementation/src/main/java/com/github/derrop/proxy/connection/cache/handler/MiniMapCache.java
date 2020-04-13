package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.world.Maps;
import com.github.derrop.proxy.api.connection.PacketSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniMapCache implements PacketCacheHandler {

    private Map<Integer, Maps> maps = new ConcurrentHashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.MAPS};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        Maps maps = (Maps) newPacket.getDeserializedPacket();
        if (!this.maps.containsKey(maps.getMapId())) {
            this.maps.put(maps.getMapId(), maps);
            return;
        }

        if (this.maps.containsKey(maps.getMapId())) {
            Maps oldMaps = this.maps.get(maps.getMapId());
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
        for (Maps maps : this.maps.values()) {
            con.sendPacket(maps);
        }
    }
}
