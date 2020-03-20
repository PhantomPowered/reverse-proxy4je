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

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.SPAWN_PLAYER, PacketConstants.GLOBAL_ENTITY_SPAWN, PacketConstants.DESTROY_ENTITIES, PacketConstants.ENTITY_TELEPORT, PacketConstants.ENTITY_EQUIPMENT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        switch (newPacket.getPacketId()) {
            case PacketConstants.DESTROY_ENTITIES:
                int count = DefinedPacket.readVarInt(newPacket.getPacketData());
                for (int i = 0; i < count; i++) {
                    this.entities.remove(DefinedPacket.readVarInt(newPacket.getPacketData()));
                }
                break;

            case PacketConstants.ENTITY_TELEPORT:
                EntityTeleport entityTeleport = new EntityTeleport();
                entityTeleport.read(newPacket.getPacketData());

                if (this.entities.containsKey(entityTeleport.getEntityId())) {
                    PositionedPacket entity = this.entities.get(entityTeleport.getEntityId());
                    entity.setX(entityTeleport.getX());
                    entity.setY(entityTeleport.getY());
                    entity.setZ(entityTeleport.getZ());
                    entity.setYaw(entityTeleport.getYaw());
                    entity.setPitch(entityTeleport.getPitch());
                }

                break;

            case PacketConstants.SPAWN_PLAYER:
                SpawnPlayer spawnPlayer = new SpawnPlayer();
                spawnPlayer.read(newPacket.getPacketData());

                this.entities.put(spawnPlayer.getEntityId(), spawnPlayer);

                break;

            case PacketConstants.GLOBAL_ENTITY_SPAWN:
                SpawnGlobalEntity spawnGlobalEntity = new SpawnGlobalEntity();
                spawnGlobalEntity.read(newPacket.getPacketData());

                this.entities.put(spawnGlobalEntity.getEntityId(), spawnGlobalEntity);

                break;

            case PacketConstants.SPAWN_OBJECT:
                SpawnObject spawnObject = new SpawnObject();
                spawnObject.read(newPacket.getPacketData());

                this.entities.put(spawnObject.getEntityId(), spawnObject);

                break;

            case PacketConstants.SPAWN_MOB:
                SpawnMob spawnMob = new SpawnMob();
                spawnMob.read(newPacket.getPacketData());

                this.entities.put(spawnMob.getEntityId(), spawnMob);

                break;
        }
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        for (PositionedPacket packet : new ArrayList<>(this.entities.values())) {
            ch.write(packet);
        }
    }
}
