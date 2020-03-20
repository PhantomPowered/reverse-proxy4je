package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.cache.handler.*;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Arrays;
import java.util.Collection;

public class PacketCache {

    private final Collection<PacketCacheHandler> handlers = Arrays.asList(
            new JoinGameCache(),
            new PlayerInventoryCache(),
            new ChunkCache(),
            new PlayerInfoCache(),
            new EntityCache()
    );

    public void handlePacket(ByteBuf packet) {
        packet.markReaderIndex();

        int receivedPacketId = DefinedPacket.readVarInt(packet);

        for (PacketCacheHandler handler : this.handlers) {
            for (int packetId : handler.getPacketIDs()) {
                if (packetId == receivedPacketId) {
                    handler.cachePacket(this, new CachedPacket(packetId, packet));
                    break;
                }
            }
        }

        packet.resetReaderIndex();
    }

    public void send(ChannelWrapper ch) {
        for (PacketCacheHandler handler : this.handlers) {
            handler.sendCached(ch);
        }
    }

    public void handleFree(ChannelWrapper ch) {
        for (PacketCacheHandler handler : this.handlers) {
            handler.onClientSwitch(ch);
        }
    }

}
