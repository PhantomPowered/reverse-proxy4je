package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.packet.*;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityCache implements PacketCacheHandler {

    private Map<Integer, PositionedPacket> entities = new HashMap<>();
    private Map<Integer, EntityMetadata> metadata = new HashMap<>();
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

        if (packet instanceof EntityTeleport) {

            EntityTeleport entityTeleport = (EntityTeleport) packet;

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

        } else if (newPacket.getPacketId() == PacketConstants.DESTROY_ENTITIES) {

            int count = DefinedPacket.readVarInt(newPacket.getPacketData());
            for (int i = 0; i < count; i++) {
                int entityId = DefinedPacket.readVarInt(newPacket.getPacketData());
                this.entities.remove(entityId);
                this.metadata.remove(entityId);
            }

        }
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        for (PositionedPacket packet : new ArrayList<>(this.entities.values())) {
            ch.write(packet);
        }
        for (EntityMetadata metadata : new ArrayList<>(this.metadata.values())) {
            ch.write(metadata);
        }
    }
}
