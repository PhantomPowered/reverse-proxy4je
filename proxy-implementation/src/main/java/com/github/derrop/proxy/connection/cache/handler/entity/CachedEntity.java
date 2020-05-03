package com.github.derrop.proxy.connection.cache.handler.entity;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;
import com.github.derrop.proxy.api.entity.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.events.connection.service.PlayerEquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityEquipment;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnEntityWeather;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CachedEntity {

    private ServiceRegistry registry;
    private ConnectedProxyClient client;
    private int entityId;
    private PositionedPacket spawnPacket;
    private Map<Integer, ItemStack> equipment;
    private MinecraftSerializableObjectList objectList = new MinecraftSerializableObjectList();

    public CachedEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        this.registry = registry;
        this.client = client;
        this.entityId = spawnPacket.getEntityId();
        this.spawnPacket = spawnPacket;
        this.equipment = new HashMap<>();
    }

    public void updateMetadata(PacketPlayServerEntityMetadata metadata) {
        if (metadata.getObjects() != null) {
            this.objectList.applyUpdate(metadata.getObjects());
        }
    }

    public void updateLocation(int x, int y, int z, byte yaw, byte pitch) {
        this.spawnPacket.setX(x);
        this.spawnPacket.setY(y);
        this.spawnPacket.setZ(z);
        this.spawnPacket.setYaw(yaw);
        this.spawnPacket.setPitch(pitch);
    }

    public void setEquipmentSlot(int slotId, ItemStack item) {
        EquipmentSlot slot = EquipmentSlot.getById(slotId);
        if (slot != null) {
            boolean cancelled = this.registry.getProviderUnchecked(EventManager.class).callEvent(new EquipmentSlotChangeEvent(this.client.getConnection(), this.entityId, slot, item)).isCancelled();
            if (this.isPlayer()) {
                UUID uniqueId = ((PacketPlayServerNamedEntitySpawn) this.spawnPacket).getPlayerId();
                PlayerInfo playerInfo = this.client.getConnection().getWorldDataProvider().getOnlinePlayer(uniqueId);
                if (playerInfo != null) {
                    cancelled = cancelled || this.registry.getProviderUnchecked(EventManager.class).callEvent(new PlayerEquipmentSlotChangeEvent(this.client.getConnection(), playerInfo, slot, item)).isCancelled();
                }
            }

            if (cancelled) {
                throw CancelProceedException.INSTANCE;
            }

            this.equipment.put(slotId, item);
        }
    }

    public boolean isPlayer() {
        return this.spawnPacket instanceof PacketPlayServerNamedEntitySpawn;
    }

    public void spawn(PlayerInfoCache infoCache, PacketSender sender) {
        if (this.spawnPacket instanceof PacketPlayServerNamedEntitySpawn && !infoCache.isCached(((PacketPlayServerNamedEntitySpawn) this.spawnPacket).getPlayerId())) {
            // NPCs might get removed out of the player list after they have been spawned, but the client doesn't spawn them without them being in the list
            PacketPlayServerPlayerInfo.Item item = infoCache.getRemovedItem(((PacketPlayServerNamedEntitySpawn) this.spawnPacket).getPlayerId());
            sender.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.ADD_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item}));
            sender.sendPacket(this.spawnPacket);
            Constants.SCHEDULED_EXECUTOR_SERVICE.schedule(
                    () -> sender.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item})),
                    500, TimeUnit.MILLISECONDS
            );
        } else {
            sender.sendPacket(this.spawnPacket);
        }

        sender.sendPacket(new PacketPlayServerEntityMetadata(this.entityId, this.objectList.getObjects()));
        this.equipment.forEach((slot, item) -> sender.sendPacket(new PacketPlayServerEntityEquipment(this.entityId, slot, item)));
    }

}
