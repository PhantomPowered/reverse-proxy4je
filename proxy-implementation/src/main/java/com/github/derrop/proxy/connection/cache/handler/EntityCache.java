package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.packet.entity.*;
import com.github.derrop.proxy.connection.cache.packet.entity.spawn.*;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.protocol.server.PacketPlayOutEntityTeleport;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityCache implements PacketCacheHandler {

    private final Map<Integer, PositionedPacket> entities = new ConcurrentHashMap<>();
    private final Map<Integer, EntityMetadata> metadata = new ConcurrentHashMap<>();

    // todo (fake?) players are not spawned properly
    @Override
    public int[] getPacketIDs() {
        return new int[]{
                PacketConstants.SPAWN_PLAYER, PacketConstants.GLOBAL_ENTITY_SPAWN, PacketConstants.DESTROY_ENTITIES,
                PacketConstants.ENTITY_TELEPORT, PacketConstants.ENTITY_EQUIPMENT, PacketConstants.SPAWN_MOB,
                PacketConstants.SPAWN_OBJECT, PacketConstants.ENTITY_METADATA
        };
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        DefinedPacket packet = newPacket.getDeserializedPacket();

        if (packet instanceof PacketPlayOutEntityTeleport) {

            PacketPlayOutEntityTeleport entityTeleport = (PacketPlayOutEntityTeleport) packet;

            if (this.entities.containsKey(entityTeleport.getEntityId())) {
                PositionedPacket entity = this.entities.get(entityTeleport.getEntityId());
                entity.setX(entityTeleport.getX());
                entity.setY(entityTeleport.getY());
                entity.setZ(entityTeleport.getZ());
                entity.setYaw(entityTeleport.getYaw());
                entity.setPitch(entityTeleport.getPitch());
            }

        } else if (packet instanceof SpawnPlayer) {

            SpawnPlayer spawnPlayer = (SpawnPlayer) packet;

            this.entities.put(spawnPlayer.getEntityId(), spawnPlayer);

        } else if (packet instanceof SpawnMob) {

            SpawnMob spawnMob = (SpawnMob) packet;

            this.entities.put(spawnMob.getEntityId(), spawnMob);

        } else if (packet instanceof SpawnObject) {

            SpawnObject spawnObject = (SpawnObject) packet;

            this.entities.put(spawnObject.getEntityId(), spawnObject);

        } else if (packet instanceof SpawnGlobalEntity) {

            SpawnGlobalEntity spawnGlobalEntity = (SpawnGlobalEntity) packet;

            this.entities.put(spawnGlobalEntity.getEntityId(), spawnGlobalEntity);

        } else if (packet instanceof EntityMetadata) {

            this.metadata.put(((EntityMetadata) packet).getEntityId(), (EntityMetadata) packet);

        } else if (packet instanceof DestroyEntities) {

            DestroyEntities destroyEntities = (DestroyEntities) packet;
            for (int entityId : destroyEntities.getEntityIds()) {
                this.entities.remove(entityId);
                this.metadata.remove(entityId);
            }

        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (PositionedPacket packet : this.entities.values()) {
            con.sendPacket(packet);
        }

        for (EntityMetadata metadata : this.metadata.values()) {
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

        con.sendPacket(new DestroyEntities(entityIds));
    }
}
