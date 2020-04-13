package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.world.UpdateSign;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SignCache implements PacketCacheHandler {

    private Map<BlockPos, UpdateSign> signUpdates = new ConcurrentHashMap<>();

    private PacketCache packetCache;

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.UPDATE_SIGN};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.packetCache = packetCache;

        UpdateSign sign = (UpdateSign) newPacket.getDeserializedPacket();
        this.signUpdates.put(sign.getPos(), sign);
    }

    @Override
    public void sendCached(PacketSender con) {
        for (Map.Entry<BlockPos, UpdateSign> entry : this.signUpdates.entrySet()) {
            Material material = this.packetCache.getMaterialAt(entry.getKey());

            if (material != Material.SIGN && material != Material.SIGN_POST) {
                this.signUpdates.remove(entry.getKey());
                continue;
            }

            con.sendPacket(entry.getValue());
        }
    }
    // todo sometimes I get the "Unable to locate sign" message
}
