package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.handler.*;
import com.github.derrop.proxy.connection.cache.packet.entity.player.GameStateChange;
import com.github.derrop.proxy.connection.cache.packet.world.UpdateSign;
import com.github.derrop.proxy.util.chunk.DefaultBlockStates;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PacketCache {

    private final ConnectedProxyClient targetProxyClient;
    private final Collection<PacketCacheHandler> handlers = new ArrayList<>();

    private BiConsumer<ByteBuf, Integer> packetHandler;

    {
        this.reset();
    }

    public PacketCache(ConnectedProxyClient targetProxyClient) {
        this.targetProxyClient = targetProxyClient;
    }

    public void setPacketHandler(BiConsumer<ByteBuf, Integer> packetHandler) {
        this.packetHandler = packetHandler;
    }

    public BiConsumer<ByteBuf, Integer> getPacketHandler() {
        return this.packetHandler;
    }

    public PacketCacheHandler getHandler(Predicate<PacketCacheHandler> filter) {
        return this.handlers.stream().filter(filter).findFirst().orElse(null);
    }

    public Collection<PacketCacheHandler> getHandlers() {
        return this.handlers;
    }

    public void handlePacket(ByteBuf packet, DefinedPacket deserialized) {
        packet.markReaderIndex();

        if (deserialized instanceof UpdateSign) {
            int state = this.getBlockStateAt(((UpdateSign) deserialized).getPos());
            if (Arrays.stream(DefaultBlockStates.SIGNS).noneMatch(i -> i == state)) {
                return;
            }
        }

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

        if (this.packetHandler != null) {
            this.packetHandler.accept(packet, receivedPacketId);
        }
    }

    public int getBlockStateAt(BlockPos pos) {
        return ((ChunkCache) this.getHandler(handler -> handler instanceof ChunkCache)).getBlockStateAt(pos);
    }

    public ConnectedProxyClient getTargetProxyClient() {
        return targetProxyClient;
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
                new SimplePacketCache(PacketConstants.CAMERA), // todo I think this doesn't work properly
                new SimplePacketCache(PacketConstants.TIME_UPDATE),
                new SimplePacketCache(PacketConstants.UPDATE_HEALTH),
                new MappedPacketCache<>(PacketConstants.GAME_STATE_CHANGE, GameStateChange::getState, gameStateChange -> false),
                new SimplePacketCache(71), // header/footer
                new ListPacketCache(2, 30), // chat
                new WorldBorderCache(),
                new PlayerInventoryCache(),
                new ChunkCache(),
                new PlayerInfoCache(),
                new EntityCache(),
                new EntityEffectCache(),
                new MiniMapCache(),
                new SignCache(),
                new ScoreboardCache()
        ));
    }

}
