package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.JoinGame;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.netty.ChannelWrapper;

public class JoinGameCache implements PacketCacheHandler {

    private JoinGame packet;

    @Override
    public int[] getPacketIDs() {
        return new int[]{1};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        JoinGame packet = new JoinGame();
        packet.read(newPacket.getPacketData());
        this.packet = packet;
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        if (this.packet != null) {
            ch.write(this.packet);
        }
    }
}
