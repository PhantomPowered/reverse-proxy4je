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

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.EntityType;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.entity.CachedEntity;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityDestroy;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityEquipment;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnEntity;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnEntityExperienceOrb;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnLivingEntity;
import com.github.derrop.proxy.protocol.play.server.player.PacketPlayServerCamera;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EntityCache implements PacketCacheHandler {
    // TODO Remove all entities on Bungee ServerSwitch

    private final Map<Integer, CachedEntity> entities = new ConcurrentHashMap<>();

    private int cameraTargetId = -1;

    private PacketCache packetCache;

    @Override
    public int[] getPacketIDs() {
        return new int[]{
                ProtocolIds.ToClient.Play.ENTITY_TELEPORT,
                ProtocolIds.ToClient.Play.CAMERA,
                ProtocolIds.ToClient.Play.ENTITY_METADATA,
                ProtocolIds.ToClient.Play.ENTITY_EQUIPMENT,

                ProtocolIds.ToClient.Play.NAMED_ENTITY_SPAWN,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY_LIVING,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY,
                ProtocolIds.ToClient.Play.SPAWN_ENTITY_EXPERIENCE_ORB,

                ProtocolIds.ToClient.Play.ENTITY_DESTROY
        };
    }

    public Map<Integer, CachedEntity> getEntities() {
        return this.entities;
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.packetCache = packetCache;
        ServiceRegistry registry = packetCache.getTargetProxyClient().getProxy().getServiceRegistry();

        Packet packet = newPacket.getDeserializedPacket();

        if (packet instanceof PacketPlayServerEntityTeleport) {
            PacketPlayServerEntityTeleport teleport = (PacketPlayServerEntityTeleport) packet;
            if (this.entities.containsKey(teleport.getEntityId())) {
                this.entities.get(teleport.getEntityId()).updateLocation(
                        teleport.getX(), teleport.getY(), teleport.getZ(),
                        teleport.getYaw(), teleport.getPitch(),
                        teleport.isOnGround()
                );
            }
        } else if (packet instanceof PacketPlayServerSpawnEntityExperienceOrb) {
            PacketPlayServerSpawnEntityExperienceOrb spawn = (PacketPlayServerSpawnEntityExperienceOrb) packet;
            this.entities.put(spawn.getEntityId(), CachedEntity.createEntity(registry, packetCache.getTargetProxyClient(), spawn, EntityType.EXPERIENCE_ORB));
        } else if (packet instanceof PacketPlayServerNamedEntitySpawn) {
            PacketPlayServerNamedEntitySpawn spawn = (PacketPlayServerNamedEntitySpawn) packet;
            this.entities.put(spawn.getEntityId(), CachedEntity.createEntity(registry, packetCache.getTargetProxyClient(), spawn, EntityType.PLAYER));
        } else if (packet instanceof PacketPlayServerSpawnLivingEntity) {
            PacketPlayServerSpawnLivingEntity spawn = (PacketPlayServerSpawnLivingEntity) packet;
            this.entities.put(spawn.getEntityId(), CachedEntity.createEntity(registry, packetCache.getTargetProxyClient(), spawn, EntityType.fromId(spawn.getType())));
        } else if (packet instanceof PacketPlayServerSpawnEntity) {
            PacketPlayServerSpawnEntity spawn = (PacketPlayServerSpawnEntity) packet;
            this.entities.put(spawn.getEntityId(), CachedEntity.createEntity(registry, packetCache.getTargetProxyClient(), spawn, spawn.getType() == 2 ? EntityType.ITEM : EntityType.fromId(spawn.getType())));
        } else if (packet instanceof PacketPlayServerEntityMetadata) {
            PacketPlayServerEntityMetadata metadata = (PacketPlayServerEntityMetadata) packet;
            if (this.entities.containsKey(metadata.getEntityId())) {
                this.entities.get(metadata.getEntityId()).updateMetadata(metadata);
            }
        } else if (packet instanceof PacketPlayServerEntityDestroy) {
            PacketPlayServerEntityDestroy destroyEntities = (PacketPlayServerEntityDestroy) packet;
            for (int entityId : destroyEntities.getEntityIds()) {
                this.entities.remove(entityId);
            }
        } else if (packet instanceof PacketPlayServerEntityEquipment) {
            PacketPlayServerEntityEquipment equipment = (PacketPlayServerEntityEquipment) packet;
            if (this.entities.containsKey(equipment.getEntityId())) {
                if (!this.entities.get(equipment.getEntityId()).setEquipmentSlot(equipment.getSlot(), equipment.getItem())) {
                    // TODO send the packet to the old slot back
                    throw CancelProceedException.INSTANCE;
                }
            }
        } else if (packet instanceof PacketPlayServerCamera) {
            this.cameraTargetId = ((PacketPlayServerCamera) packet).getEntityId();
        }
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (this.packetCache == null) {
            return;
        }

        for (CachedEntity entity : this.entities.values()) {
            entity.spawn(con);
        }

        if (this.shouldSendCameraPacket(targetProxyClient)) {
            Constants.SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
                if (this.shouldSendCameraPacket(targetProxyClient)) {
                    con.sendPacket(new PacketPlayServerCamera(this.cameraTargetId));
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    private boolean shouldSendCameraPacket(ConnectedProxyClient targetProxyClient) {
        return this.cameraTargetId != -1 && this.cameraTargetId != targetProxyClient.getEntityId();
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
