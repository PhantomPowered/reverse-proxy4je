package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.JoinGame;
import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.handler.*;
import de.derrop.minecraft.proxy.connection.cache.packet.PlayerAbilities;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Arrays;
import java.util.Collection;

public class PacketCache {

    private final Collection<PacketCacheHandler> handlers = Arrays.asList(
            new SimplePacketCache(1, JoinGame::new),
            new SimplePacketCache(PacketConstants.PLAYER_ABILITIES, PlayerAbilities::new),
            new PlayerInventoryCache(),
            new ChunkCache(),
            new PlayerInfoCache(),
            new EntityCache()
    );
    // todo scoreboards, resource pack, keep alive proxy <-> client, time

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
