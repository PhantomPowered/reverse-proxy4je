package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.function.Supplier;

public class SimplePacketCache implements PacketCacheHandler {

    private int packetId;
    private Supplier<DefinedPacket> packetSupplier;

    private DefinedPacket lastPacket;

    public SimplePacketCache(int packetId, Supplier<DefinedPacket> packetSupplier) {
        this.packetId = packetId;
        this.packetSupplier = packetSupplier;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{this.packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        DefinedPacket packet = this.packetSupplier.get();
        packet.read(newPacket.getPacketData());
        this.lastPacket = packet;
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        if (this.lastPacket != null) {
            ch.write(this.lastPacket);
        }
    }
}
