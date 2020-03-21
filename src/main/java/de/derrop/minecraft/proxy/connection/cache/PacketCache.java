package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.handler.*;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Arrays;
import java.util.Collection;

public class PacketCache {

    private final Collection<PacketCacheHandler> handlers = Arrays.asList(
            new SimplePacketCache(1),
            new SimplePacketCache(PacketConstants.PLAYER_ABILITIES),
            new PlayerInventoryCache(),
            new ChunkCache(),
            new PlayerInfoCache(),
            new EntityCache()
    );
    // todo scoreboards, (resource pack), keep alive proxy <-> client, signs

    public void handlePacket(ByteBuf packet, DefinedPacket deserialized) {
        packet.markReaderIndex();

        int receivedPacketId = DefinedPacket.readVarInt(packet);

        for (PacketCacheHandler handler : this.handlers) {
            for (int packetId : handler.getPacketIDs()) {
                if (packetId == receivedPacketId) {
                    handler.cachePacket(this, new CachedPacket(packetId, deserialized, packet));
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
