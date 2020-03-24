package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.handler.*;
import de.derrop.minecraft.proxy.connection.cache.packet.world.UpdateSign;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.connection.UserConnection;
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

    public void send(UserConnection connection, boolean switched) {
        for (PacketCacheHandler handler : this.handlers) {
            if (!switched || handler.sendOnSwitch()) {
                handler.sendCached(connection);
            }
        }
    }

    public void handleFree(UserConnection connection) {
        for (PacketCacheHandler handler : this.handlers) {
            handler.onClientSwitch(connection);
        }
    }

    public void reset() {
        this.handlers.clear();
        this.handlers.addAll(Arrays.asList(
                // THE ORDER IS IMPORTANT
                new LoginCache(),
                new SimplePacketCache(PacketConstants.PLAYER_ABILITIES),
                new SimplePacketCache(PacketConstants.WORLD_BORDER),
                new SimplePacketCache(PacketConstants.CAMERA), // todo I think this doesn't work properly
                new MappedPacketCache<>(PacketConstants.UPDATE_SIGN, UpdateSign::getPos, updateSign -> false), // todo I think this doesn't work properly
                new SimplePacketCache(PacketConstants.TIME_UPDATE), // time update
                new SimplePacketCache(71), // header/footer
                new ListPacketCache(2, 30), // chat
                new PlayerInventoryCache(),
                new ChunkCache(),
                new PlayerInfoCache(),
                new EntityCache(),
                new EntityEffectCache(),
                new MiniMapCache()
        ));
        // todo chunks are not loaded until they are received from the server again
        // todo (resource pack), keep alive proxy <-> client, signs, gamemode
        // todo scoreboards are not displayed properly
    }

}
