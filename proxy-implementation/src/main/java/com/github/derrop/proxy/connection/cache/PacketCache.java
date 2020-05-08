/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.block.DefaultBlockAccess;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.handler.*;
import com.github.derrop.proxy.connection.cache.handler.EntityCache;
import com.github.derrop.proxy.connection.cache.handler.scoreboard.ScoreboardCache;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.player.PacketPlayServerGameStateChange;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PacketCache {

    private final ConnectedProxyClient targetProxyClient;
    private final Collection<PacketCacheHandler> handlers = new CopyOnWriteArrayList<>();

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

    public void handlePacket(ByteBuf packet, Packet deserialized) {
        packet.markReaderIndex();

        int receivedPacketId = ByteBufUtils.readVarInt(packet);

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

    public void handleClientPacket(Packet deserialized) {
        for (PacketCacheHandler handler : this.handlers) {
            for (int packetId : handler.getPacketIDs()) {
                if (packetId == deserialized.getId()) {
                    handler.cacheClientPacket(this, deserialized);
                    break;
                }
            }
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
        return this.targetProxyClient;
    }

    public void send(Player connection, boolean switched) {
        for (PacketCacheHandler handler : this.handlers) {
            if (!switched || handler.sendOnSwitch()) {
                handler.sendCached(connection, this.targetProxyClient);
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
                new SimplePacketCache(ProtocolIds.ToClient.Play.ABILITIES),
                new SimplePacketCache(ProtocolIds.ToClient.Play.UPDATE_TIME),
                new SimplePacketCache(ProtocolIds.ToClient.Play.UPDATE_HEALTH),
                new MappedPacketCache<>(ProtocolIds.ToClient.Play.GAME_STATE_CHANGE, PacketPlayServerGameStateChange::getState, gameStateChange -> false),
                new SimplePacketCache(ProtocolIds.ToClient.Play.PLAYER_LIST_HEADER_FOOTER),
                new ListPacketCache(ProtocolIds.ToClient.Play.CHAT, 30),
                new HeldItemSlotCache(),
                new WorldBorderCache(),
                new PlayerInventoryCache(),
                chunkCache,
                new PlayerInfoCache(),
                new EntityCache(),
                new EntityEffectCache(),
                new MiniMapCache(),
                new SignCache(),
                new ScoreboardCache(),
                new CameraCache(),
                new GameStateCache()
        ));

        this.blockAccess = new DefaultBlockAccess(this.targetProxyClient.getProxy(), chunkCache);
    }

}
