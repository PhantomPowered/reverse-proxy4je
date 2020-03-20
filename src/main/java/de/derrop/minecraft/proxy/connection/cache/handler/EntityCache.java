package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.PacketConstants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.SpawnedEntity;
import de.derrop.minecraft.proxy.connection.cache.packet.SpawnPlayer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityCache implements PacketCacheHandler {

    private Map<Integer, SpawnedEntity> entities = new HashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.SPAWN_PLAYER, PacketConstants.ENTITY_SPAWN, PacketConstants.DESTROY_ENTITIES, PacketConstants.ENTITY_POSITION, PacketConstants.ENTITY_EQUIPMENT};
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

            case PacketConstants.ENTITY_POSITION:

                break;

            case PacketConstants.SPAWN_PLAYER:
                SpawnPlayer spawnPlayer = new SpawnPlayer();
                spawnPlayer.read(newPacket.getPacketData());

                this.entities.put(spawnPlayer.getEntityId(), new SpawnedEntity(
                        spawnPlayer.getEntityId(), spawnPlayer.getPlayerId(),
                        spawnPlayer.getX(), spawnPlayer.getY(), spawnPlayer.getZ(),
                        spawnPlayer.getYaw(), spawnPlayer.getPitch(),
                        spawnPlayer.getCurrentItem(), spawnPlayer.getWatchableObjects()
                ));

                break;
        }
    }

    @Override
    public void sendCached(ChannelWrapper ch) {
        for (SpawnedEntity value : new ArrayList<>(this.entities.values())) {
            if (value.isPlayer()) {
                ch.write(new SpawnPlayer(value.getEntityId(), value.getPlayerId(), value.getX(), value.getY(), value.getZ(), value.getYaw(), value.getPitch(), value.getCurrentItem(), value.getWatchableObjects()));
            } else {
                // todo
            }
        }
    }
}
