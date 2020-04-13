package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.world.UpdateSign;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.util.chunk.DefaultBlockStates;
import com.github.derrop.proxy.api.connection.PacketSender;

import java.util.Arrays;
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
            int state = this.packetCache.getBlockStateAt(entry.getKey());

            if (Arrays.stream(DefaultBlockStates.SIGNS).noneMatch(i -> i == state)) {
                this.signUpdates.remove(entry.getKey());
                continue;
            }

            con.sendPacket(entry.getValue());
        }
    }
    // todo sometimes I get the "Unable to locate sign" message
}
