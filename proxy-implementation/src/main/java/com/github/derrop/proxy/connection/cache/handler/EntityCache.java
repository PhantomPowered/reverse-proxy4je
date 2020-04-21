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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityDestroy;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnEntity;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnEntityWeather;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnLivingEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EntityCache implements PacketCacheHandler {

    private final Map<Integer, PositionedPacket> entities = new ConcurrentHashMap<>();
    private final Map<Integer, PacketPlayServerEntityMetadata> metadata = new ConcurrentHashMap<>();

    private PacketCache packetCache;

    // todo the entity equipment packet is ignored
    @Override
    public int[] getPacketIDs() {
        return new int[]{
                ProtocolIds.ToClient.Play.NAMED_ENTITY_SPAWN,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY_WEATHER,
                ProtocolIds.ToClient.Play.ENTITY_DESTROY,
                ProtocolIds.ToClient.Play.ENTITY_TELEPORT,
                ProtocolIds.ToClient.Play.ENTITY_EQUIPMENT,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY_LIVING,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY,
                ProtocolIds.ToClient.Play.ENTITY_METADATA
        };
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.packetCache = packetCache;

        Packet packet = newPacket.getDeserializedPacket();

        if (packet instanceof PacketPlayServerEntityTeleport) {

            PacketPlayServerEntityTeleport entityTeleport = (PacketPlayServerEntityTeleport) packet;

            if (this.entities.containsKey(entityTeleport.getEntityId())) {
                PositionedPacket entity = this.entities.get(entityTeleport.getEntityId());
                entity.setX(entityTeleport.getX());
                entity.setY(entityTeleport.getY());
                entity.setZ(entityTeleport.getZ());
                entity.setYaw(entityTeleport.getYaw());
                entity.setPitch(entityTeleport.getPitch());
            }

        } else if (packet instanceof PacketPlayServerNamedEntitySpawn) {

            PacketPlayServerNamedEntitySpawn spawnPlayer = (PacketPlayServerNamedEntitySpawn) packet;

            this.entities.put(spawnPlayer.getEntityId(), spawnPlayer);

        } else if (packet instanceof PacketPlayServerSpawnLivingEntity) {

            PacketPlayServerSpawnLivingEntity spawnMob = (PacketPlayServerSpawnLivingEntity) packet;

            this.entities.put(spawnMob.getEntityId(), spawnMob);

        } else if (packet instanceof PacketPlayServerSpawnEntity) {

            PacketPlayServerSpawnEntity spawnObject = (PacketPlayServerSpawnEntity) packet;

            this.entities.put(spawnObject.getEntityId(), spawnObject);

        } else if (packet instanceof PacketPlayServerSpawnEntityWeather) {

            PacketPlayServerSpawnEntityWeather spawnGlobalEntity = (PacketPlayServerSpawnEntityWeather) packet;

            this.entities.put(spawnGlobalEntity.getEntityId(), spawnGlobalEntity);

        } else if (packet instanceof PacketPlayServerEntityMetadata) {

            PacketPlayServerEntityMetadata metadata = (PacketPlayServerEntityMetadata) packet;
            if (this.metadata.containsKey(metadata.getEntityId())) {
                this.metadata.get(metadata.getEntityId()).getWatchableObjects().addAll(metadata.getWatchableObjects());
            } else {
                this.metadata.put(metadata.getEntityId(), metadata);
            }

        } else if (packet instanceof PacketPlayServerEntityDestroy) {

            PacketPlayServerEntityDestroy destroyEntities = (PacketPlayServerEntityDestroy) packet;
            for (int entityId : destroyEntities.getEntityIds()) {
                this.entities.remove(entityId);
                this.metadata.remove(entityId);
            }

        }
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.packetCache == null) {
            return;
        }
        PlayerInfoCache infoCache = (PlayerInfoCache) this.packetCache.getHandler(handler -> handler instanceof PlayerInfoCache);

        for (PositionedPacket packet : this.entities.values()) {
            if (packet instanceof PacketPlayServerNamedEntitySpawn && !infoCache.isCached(((PacketPlayServerNamedEntitySpawn) packet).getPlayerId())) {
                // NPCs might get removed out of the player list after they have been spawned, but the client doesn't spawn them without them being in the list
                PacketPlayServerPlayerInfo.Item item = infoCache.getRemovedItem(((PacketPlayServerNamedEntitySpawn) packet).getPlayerId());
                con.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.ADD_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item}));
                con.sendPacket(packet);
                Constants.SCHEDULED_EXECUTOR_SERVICE.schedule(
                        () -> con.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item})),
                        500, TimeUnit.MILLISECONDS
                );
            } else {
                con.sendPacket(packet);
            }
        }

        for (PacketPlayServerEntityMetadata metadata : this.metadata.values()) {
            con.sendPacket(metadata);
        }
    }

    @Override
    public void onClientSwitch(Player con) {
        if (this.entities.isEmpty()) {
            return;
        }

        Set<Integer> entityIdSet = new HashSet<>(this.entities.keySet());

        int[] entityIds = new int[entityIdSet.size()];
        int i = 0;
        for (Integer entityId : entityIdSet) {
            entityIds[i++] = entityId;
        }

        con.sendPacket(new PacketPlayServerEntityDestroy(entityIds));
    }
}
