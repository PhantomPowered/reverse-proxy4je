package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ListPacketCache implements PacketCacheHandler {

    private Queue<DefinedPacket> lastPackets = new LinkedBlockingQueue<>();
    private int packetId;
    private int limit = -1;

    public ListPacketCache(int packetId, int limit) {
        this.packetId = packetId;
        this.limit = limit;
    }

    public ListPacketCache(int packetId) {
        this.packetId = packetId;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{this.packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.lastPackets.offer(newPacket.getDeserializedPacket());

        if (this.limit < 0) {
            return;
        }

        while (this.lastPackets.size() >= this.limit) {
            this.lastPackets.poll();
        }
    }

    @Override
    public void sendCached(UserConnection con) {
        for (DefinedPacket lastPacket : new ArrayList<>(this.lastPackets)) {
            con.unsafe().sendPacket(lastPacket);
        }
    }
}
