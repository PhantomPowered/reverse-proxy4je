package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityEquipment;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CachedEntity implements Entity {

    protected final ServiceRegistry registry;
    protected final ConnectedProxyClient client;

    protected final PositionedPacket spawnPacket;
    private boolean onGround;
    private final Unsafe unsafe = this::setLocation;

    private final int entityId;

    private final Map<Integer, ItemStack> equipment;

    protected final MinecraftSerializableObjectList objectList = new MinecraftSerializableObjectList();

    protected CachedEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        this.registry = registry;
        this.client = client;
        this.entityId = spawnPacket.getEntityId();
        this.spawnPacket = spawnPacket;
        this.equipment = new HashMap<>();
    }

    public static CachedEntity createEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket) {
        // TODO more entity types?
        if (spawnPacket instanceof PacketPlayServerNamedEntitySpawn) {
            return new CachedPlayer(registry, client, spawnPacket);
        }
        return new CachedEntityWithMetadata(registry, client, spawnPacket);
    }

    public void updateMetadata(PacketPlayServerEntityMetadata metadata) {
        if (metadata.getObjects() != null) {
            this.objectList.applyUpdate(metadata.getObjects());
        }
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

    @NotNull
    @Override
    public Location getLocation() {
        return new Location(
                PlayerPositionPacketUtil.getServerLocation(this.spawnPacket.getX()),
                PlayerPositionPacketUtil.getServerLocation(this.spawnPacket.getY()),
                PlayerPositionPacketUtil.getServerLocation(this.spawnPacket.getZ()),
                PlayerPositionPacketUtil.getServerRotation(this.spawnPacket.getYaw()),
                PlayerPositionPacketUtil.getServerRotation(this.spawnPacket.getPitch())
        );
    }

    @Override
    public void setLocation(@NotNull Location location) {
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
        this.spawnPacket.setX(x);
        this.spawnPacket.setY(y);
        this.spawnPacket.setZ(z);
        this.spawnPacket.setYaw(yaw);
        this.spawnPacket.setPitch(pitch);
        this.onGround = onGround;
    }

    public boolean setEquipmentSlot(int slotId, ItemStack item) {
        EquipmentSlot slot = EquipmentSlot.getById(slotId);
        if (slot != null) {
            boolean cancelled = this.registry.getProviderUnchecked(EventManager.class)
                    .callEvent(new EquipmentSlotChangeEvent(this.client.getConnection(), this, slot, item))
                    .isCancelled();

            if (!cancelled) {
                this.equipment.put(slotId, item);
                return true;
            }

        }
        return false;
    }

    public boolean isPlayer() {
        return this.spawnPacket instanceof PacketPlayServerNamedEntitySpawn;
    }

    public void spawn(PacketSender sender) {
        sender.sendPacket(this.spawnPacket);
        this.sendMetadataAndEquipment(sender);
    }

    protected void sendMetadataAndEquipment(PacketSender sender) {
        sender.sendPacket(new PacketPlayServerEntityMetadata(this.entityId, this.objectList.getObjects()));
        this.equipment.forEach((slot, item) -> sender.sendPacket(new PacketPlayServerEntityEquipment(this.entityId, slot, item)));
    }

}
