package com.github.derrop.proxy.connection.cache.handler.entity;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityEquipment;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CachedEntity {

    private int entityId;
    private PositionedPacket spawnPacket;
    private Map<Integer, ItemStack> equipment;
    private MinecraftSerializableObjectList objectList = new MinecraftSerializableObjectList();

    public CachedEntity(PositionedPacket spawnPacket) {
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

    public void setEquipmentSlot(int slot, ItemStack item) {
        this.equipment.put(slot, item);
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
