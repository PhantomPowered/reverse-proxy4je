package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.handler.*;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public class PacketCache {

    private final Collection<PacketCacheHandler> handlers = Arrays.asList(
            new SimplePacketCache(1, false), // join game
            new SimplePacketCache(PacketConstants.PLAYER_ABILITIES),
            new SimplePacketCache(71), // header/footer
            new PlayerInventoryCache(),
            new ChunkCache(),
            new PlayerInfoCache(),
            new EntityCache()
    );
    // todo scoreboards, (resource pack), keep alive proxy <-> client, signs, tab header/footer

    public PacketCacheHandler getHandler(Predicate<PacketCacheHandler> filter) {
        return this.handlers.stream().filter(filter).findFirst().orElse(null);
    }

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

    public void send(ChannelWrapper ch, boolean switched) {
        for (PacketCacheHandler handler : this.handlers) {
            if (!switched || handler.sendOnSwitch()) {
                handler.sendCached(ch);
            }
        }
    }

    public void handleFree(ChannelWrapper ch) {
        for (PacketCacheHandler handler : this.handlers) {
            handler.onClientSwitch(ch);
        }
    }

}
