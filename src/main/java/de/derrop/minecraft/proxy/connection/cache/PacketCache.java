package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.handler.*;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public class PacketCache {

    private final Collection<PacketCacheHandler> handlers = new ArrayList<>();

    {
        this.reset();
    }

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

    public void reset() {
        this.handlers.clear();
        this.handlers.addAll(Arrays.asList(
                new SimplePacketCache(1, false), // join game
                new SimplePacketCache(PacketConstants.PLAYER_ABILITIES),
                new SimplePacketCache(5), // spawn position todo doesn't work perfectly
                new SimplePacketCache(71), // header/footer
                new ListPacketCache(2, 30), // chat
                new PlayerInventoryCache(),
                new ChunkCache(),
                new PlayerInfoCache(),
                new EntityCache()
        ));
        // todo (resource pack), keep alive proxy <-> client, signs, effects
        // todo scoreboards are not cleared when switching account
    }

}
