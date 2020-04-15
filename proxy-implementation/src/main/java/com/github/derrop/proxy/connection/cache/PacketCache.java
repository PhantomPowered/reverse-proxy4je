package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.block.DefaultBlockAccess;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.handler.*;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerGameStateChange;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PacketCache {

    private final ConnectedProxyClient targetProxyClient;
    private final Collection<PacketCacheHandler> handlers = new ArrayList<>();

    private BlockAccess blockAccess;

    private BiConsumer<ByteBuf, Integer> packetHandler;

    public PacketCache(ConnectedProxyClient targetProxyClient) {
        this.targetProxyClient = targetProxyClient;
        this.reset();
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

    public BlockAccess getBlockAccess() {
        return this.blockAccess;
    }

    public Collection<PacketCacheHandler> getHandlers() {
        return this.handlers;
    }

    public void handlePacket(ByteBuf packet, DefinedPacket deserialized) {
        packet.markReaderIndex();

        /*
        if (deserialized instanceof UpdateSign) {
            int state = this.getBlockStateAt(((UpdateSign) deserialized).getPos());
            if (Arrays.stream(DefaultBlockStateRegistry.SIGNS).noneMatch(i -> i == state)) {
                return;
            }
        }

         */

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

    public Material getMaterialAt(BlockPos pos) {
        int state = this.getBlockStateAt(pos);
        return this.targetProxyClient.getProxy().getServiceRegistry().getProviderUnchecked(BlockStateRegistry.class).getMaterial(state);
    }

    public ConnectedProxyClient getTargetProxyClient() {
        return targetProxyClient;
    }

    public void send(Player connection, boolean switched) {
        for (PacketCacheHandler handler : this.handlers) {
            if (!switched || handler.sendOnSwitch()) {
                handler.sendCached(connection);
            }
        }
    }

    public void handleFree(Player connection) {
        for (PacketCacheHandler handler : this.handlers) {
            handler.onClientSwitch(connection);
        }
    }

    public void reset() {
        this.handlers.clear();

        ChunkCache chunkCache = new ChunkCache();

        this.handlers.addAll(Arrays.asList(
                // THE ORDER IS IMPORTANT
                new LoginCache(),
                new SimplePacketCache(PacketConstants.PLAYER_ABILITIES),
                new SimplePacketCache(PacketConstants.TIME_UPDATE),
                new SimplePacketCache(PacketConstants.UPDATE_HEALTH),
                new MappedPacketCache<>(PacketConstants.GAME_STATE_CHANGE, PacketPlayServerGameStateChange::getState, gameStateChange -> false),
                new SimplePacketCache(71), // header/footer
                new ListPacketCache(2, 30), // chat
                new WorldBorderCache(),
                new PlayerInventoryCache(),
                chunkCache,
                new PlayerInfoCache(),
                new EntityCache(),
                new EntityEffectCache(),
                new MiniMapCache(),
                new SignCache(),
                new ScoreboardCache(),
                new CameraCache()
        ));

        this.blockAccess = new DefaultBlockAccess(this.targetProxyClient.getProxy(), chunkCache);
    }

    // TODO cache the held item slot

}
