package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.world.UpdateSign;
import de.derrop.minecraft.proxy.util.BlockPos;
import de.derrop.minecraft.proxy.util.chunk.DefaultBlockStates;
import net.md_5.bungee.connection.UserConnection;

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
    public void sendCached(UserConnection con) {
        for (Map.Entry<BlockPos, UpdateSign> entry : this.signUpdates.entrySet()) {
            int state = this.packetCache.getBlockStateAt(entry.getKey());

            if (state != DefaultBlockStates.POST_SIGN && state != DefaultBlockStates.WALL_SIGN) {
                continue;
            }

            con.unsafe().sendPacket(entry.getValue());
        }
    }
}
