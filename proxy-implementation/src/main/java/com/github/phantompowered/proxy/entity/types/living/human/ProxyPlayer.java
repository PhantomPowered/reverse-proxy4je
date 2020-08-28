package com.github.phantompowered.proxy.entity.types.living.human;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.entity.LivingEntityType;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.entity.PlayerSkinConfiguration;
import com.github.phantompowered.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.api.network.util.PositionedPacket;
import com.github.phantompowered.proxy.api.player.inventory.EquipmentSlot;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.phantompowered.proxy.data.DataWatcher;
import com.github.phantompowered.proxy.entity.DefaultPlayerSkinConfiguration;
import com.github.phantompowered.proxy.entity.types.living.ProxyEntityLiving;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.phantompowered.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProxyPlayer extends ProxyEntityLiving implements EntityPlayer {

    private final UUID uniqueId;
    private final PlayerInfoCache infoCache;
    private final PlayerSkinConfiguration skinConfiguration = new DefaultPlayerSkinConfiguration() {
        @Override
        protected DataWatcher getObjectList() {
            return ProxyPlayer.super.objectList;
        }
    };

    public ProxyPlayer(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        super(registry, client, spawnPacket, LivingEntityType.PLAYER);
        this.uniqueId = ((PacketPlayServerNamedEntitySpawn) spawnPacket).getPlayerId();
        this.infoCache = client.getPacketCache().getHandler(PlayerInfoCache.class);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @Nullable PlayerInfo getPlayerInfo() {
        return super.client.getConnection().getWorldDataProvider().getOnlinePlayer(this.uniqueId);
    }

    @Override
    public boolean isEating() {
        if (super.isEating()) {
            Material material = this.getMaterialInEquipmentSlot(EquipmentSlot.HAND);
            return material != null && material.isEdible();
        }
        return false;
    }

    @Override
    public boolean isShootingWithBow() {
        if (super.isEating()) {
            Material material = this.getMaterialInEquipmentSlot(EquipmentSlot.HAND);
            return material == Material.BOW;
        }
        return false;
    }

    @Override
    public boolean isBlocking() {
        Material material = this.getMaterialInEquipmentSlot(EquipmentSlot.HAND);
        return material != null && material.isSword();
    }

    @Override
    public Material getMaterialInEquipmentSlot(@NotNull EquipmentSlot slot) {
        ItemStack item = this.getEquipmentSlot(EquipmentSlot.HAND);
        return item == null ? null : Material.getMaterial(item.getItemId());
    }

    @Override
    public ItemStack getEquipmentSlot(@NotNull EquipmentSlot slot) {
        return this.equipment.get(slot.ordinal());
    }

    @Override
    public @NotNull PlayerSkinConfiguration getSkinConfiguration() {
        return this.skinConfiguration;
    }

    @Override
    public int getScore() {
        return this.objectList.getInt(18);
    }

    @Override
    public void spawn(PacketSender sender) {
        if (!this.infoCache.isCached(this.uniqueId) && super.packet != null) { // klaro - pail
            // NPCs might get removed out of the player list after they have been spawned, but the client doesn't spawn them without them being in the list
            PacketPlayServerPlayerInfo.Item item = infoCache.getRemovedItem(this.uniqueId);
            sender.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.ADD_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item}));
            sender.sendPacket(super.packet);
            APIUtil.SCHEDULED_EXECUTOR_SERVICE.schedule(
                    () -> sender.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER, new PacketPlayServerPlayerInfo.Item[]{item})),
                    500, TimeUnit.MILLISECONDS
            );

            super.sendEntityData(sender);
        } else {
            super.spawn(sender);
        }
    }

    @Override
    public float getHeadHeight() {
        return this.isSneaking() ? 1.54F : 1.62F;
    }
}
