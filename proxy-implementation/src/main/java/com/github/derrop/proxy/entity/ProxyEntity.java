package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.entity.*;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.TimedEntityEffect;
import com.github.derrop.proxy.connection.cache.handler.EntityEffectCache;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityEquipment;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerRemoveEntityEffect;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyEntity implements SpawnedEntity, Entity.Callable {

    protected final ServiceRegistry registry;
    protected final ConnectedProxyClient client;

    protected Location location;
    private boolean onGround;
    private final Unsafe unsafe = this::setLocation;

    private final int entityId;
    private final int type;

    protected @Nullable PositionedPacket packet;

    protected final Map<Integer, ItemStack> equipment;
    protected final MinecraftSerializableObjectList objectList = new MinecraftSerializableObjectList();

    protected ProxyEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, int type) {
        this(registry, client, new Location(
                PlayerPositionPacketUtil.getServerLocation(spawnPacket.getX()),
                PlayerPositionPacketUtil.getServerLocation(spawnPacket.getY()),
                PlayerPositionPacketUtil.getServerLocation(spawnPacket.getZ()),
                PlayerPositionPacketUtil.getServerRotation(spawnPacket.getYaw()),
                PlayerPositionPacketUtil.getServerRotation(spawnPacket.getPitch())
        ), spawnPacket.getEntityId(), type);

        this.packet = spawnPacket;
    }

    protected ProxyEntity(ServiceRegistry registry, ConnectedProxyClient client, Location location, int entityId, int type) {
        this.registry = registry;
        this.client = client;
        this.entityId = entityId;
        this.type = type;
        this.location = location;
        this.equipment = new ConcurrentHashMap<>();
    }

    public static ProxyEntity createEntityLiving(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, int type) {
        LivingEntityType entityType = LivingEntityType.fromId(type);
        Preconditions.checkNotNull(entityType, "Unable to create entity type from unknown id " + type);

        if (spawnPacket instanceof PacketPlayServerNamedEntitySpawn) {
            return new ProxyPlayer(registry, client, spawnPacket);
        }

        if (entityType == LivingEntityType.BAT) {
            return new ProxyBat(registry, client, spawnPacket);
        }

        return new ProxyLivingEntity(registry, client, spawnPacket, entityType);
    }

    public static ProxyEntity createEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, int type, int subType) {
        EntityType entityType = EntityType.fromId(type, subType);
        Preconditions.checkNotNull(entityType, "Cannot create entity from type id " + type + " with sub id " + subType);

        if (entityType == EntityType.ARMOR_STAND) {
            return new ProxyArmorStand(registry, client, spawnPacket);
        }

        return new ProxyEntity(registry, client, spawnPacket, entityType.getTypeId());
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public int getDimension() {
        return this.client.getDimension();
    }

    @Override
    public @NotNull Unsafe unsafe() {
        return this.unsafe;
    }

    @Override
    public @NotNull Callable getCallable() {
        return this;
    }

    @Override
    public double getEyeHeight() {
        return 1.8;
    }

    @Override
    public boolean isBurning() {
        return (this.objectList.getByte(0) & 1) != 0;
    }

    @Override
    public boolean isSneaking() {
        return (this.objectList.getByte(0) & 2) != 0;
    }

    @Override
    public boolean isRiding() {
        return (this.objectList.getByte(0) & 4) != 0;
    }

    @Override
    public boolean isSprinting() {
        return (this.objectList.getByte(0) & 8) != 0;
    }

    @Override
    public boolean isBlocking() {
        return (this.objectList.getByte(0) & 16) != 0;
    }

    @Override
    public boolean isInvisible() {
        return (this.objectList.getByte(0) & 32) != 0;
    }

    @Override
    public short getAirTicks() {
        return this.objectList.getShort(1);
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.objectList.getByte(3) == 1;
    }

    @Override
    public boolean isSilent() {
        return this.objectList.getByte(4) == 1;
    }

    @Override
    public boolean hasCustomName() {
        String s = this.objectList.getString(2);
        return s != null && s.length() > 0;
    }

    @Override
    public String getCustomName() {
        return this.objectList.getString(2);
    }

    @Override
    public int getType() {
        return this.type;
    }

    @NotNull
    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(@NotNull Location location) {
        this.location = location;
        this.updateLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.isOnGround());
    }

    public void updateLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.updateLocation(
                PlayerPositionPacketUtil.getClientLocation(x),
                PlayerPositionPacketUtil.getClientLocation(y),
                PlayerPositionPacketUtil.getClientLocation(z),
                PlayerPositionPacketUtil.getClientRotation(yaw),
                PlayerPositionPacketUtil.getClientRotation(pitch),
                onGround
        );
    }

    public void updateLocation(int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
        if (this.packet == null) {
            return;
        }

        this.packet.setX(x);
        this.packet.setY(y);
        this.packet.setZ(z);
        this.packet.setYaw(yaw);
        this.packet.setPitch(pitch);
        this.onGround = onGround;
    }

    public ItemStack setEquipmentSlot(int slotId, ItemStack item) {
        EquipmentSlot slot = EquipmentSlot.getById(slotId);
        if (slot != null) {
            EquipmentSlotChangeEvent event = this.registry.getProviderUnchecked(EventManager.class)
                    .callEvent(new EquipmentSlotChangeEvent(this.client.getConnection(), this, slot, item));

            if (event.isCancelled()) {
                return null;
            }

            this.equipment.put(slotId, item);
            return event.getItem();
        }

        return null;
    }

    public boolean isPlayer() {
        return this.packet != null && this.type == -2 && this.packet instanceof PacketPlayServerNamedEntitySpawn; // klaro - pail
    }

    public void spawn(PacketSender sender) {
        if (this.packet == null) {
            return;
        }

        sender.sendPacket(this.packet);
        this.sendEntityData(sender);
    }

    protected void sendEntityData(PacketSender sender) {
        sender.sendPacket(new PacketPlayServerEntityMetadata(this.entityId, this.objectList.getObjects()));
        this.equipment.forEach((slot, item) -> sender.sendPacket(new PacketPlayServerEntityEquipment(this.entityId, slot, item)));
    }

    @Override
    public EntityEffect createEffect(byte effectId, byte amplifier, int duration, boolean hideParticles) {
        return TimedEntityEffect.create(this.entityId, effectId, amplifier, duration, hideParticles);
    }

    private EntityEffectCache getEffectCache() {
        return (EntityEffectCache) this.client.getPacketCache().getHandler(handler -> handler instanceof EntityEffectCache);
    }

    @Override
    public EntityEffect[] getActiveEffects() {
        return this.getEffectCache().getEffects(this.entityId).values().toArray(new EntityEffect[0]);
    }

    @Override
    public void addEffect(EntityEffect effect) {
        TimedEntityEffect timed = (TimedEntityEffect) effect;
        if (timed.getEntityId() != this.entityId) {
            throw new IllegalArgumentException("Cannot give entity an effect which was created for another entity");
        }

        this.getEffectCache().getEffects(this.entityId).put(effect.getEffectId(), timed);
        if (this.client.getRedirector() != null) {
            this.client.getRedirector().sendPacket(timed.toEntityEffect());
        }
    }

    @Override
    public void removeEffect(byte effectId) {
        if (this.getEffectCache().getEffects(this.entityId).remove(effectId) != null && this.client.getRedirector() != null) {
            this.client.getRedirector().sendPacket(new PacketPlayServerRemoveEntityEffect(this.entityId, effectId));
        }
    }

    @Override
    public void handleEntityPacket(@NotNull Packet packet) {
        if (packet instanceof PacketPlayServerEntityMetadata) {
            PacketPlayServerEntityMetadata metadata = (PacketPlayServerEntityMetadata) packet;
            this.objectList.applyUpdate(metadata.getObjects());
        }
    }
}
