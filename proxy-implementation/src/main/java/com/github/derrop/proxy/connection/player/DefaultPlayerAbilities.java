package com.github.derrop.proxy.connection.player;

import com.github.derrop.proxy.api.player.PlayerAbilities;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.connection.cache.handler.SimplePacketCache;
import com.github.derrop.proxy.protocol.play.server.player.PacketPlayServerPlayerAbilities;

import java.util.Optional;

public class DefaultPlayerAbilities implements PlayerAbilities {

    private final BasicServiceConnection connection;

    public DefaultPlayerAbilities(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private Optional<PacketPlayServerPlayerAbilities> getLastAbilitiesPacket() {
        return this.connection.getClient().getPacketCache().getHandlers().stream()
                .filter(handler -> handler instanceof SimplePacketCache)
                .map(handler -> ((SimplePacketCache) handler).getLastPacket())
                .filter(packet -> packet instanceof PacketPlayServerPlayerAbilities)
                .findFirst()
                .map(packet -> (PacketPlayServerPlayerAbilities) packet);
    }

    @Override
    public boolean isInvulnerable() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::isInvulnerable).orElse(false);
    }

    @Override
    public boolean isFlying() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::isFlying).orElse(false);
    }

    @Override
    public boolean isAllowedFlying() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::isAllowFlying).orElse(false);
    }

    @Override
    public boolean isCreativeMode() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::isCreativeMode).orElse(false);
    }

    @Override
    public float getFlightSpeed() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::getFlySpeed).orElse(-1F);
    }

    @Override
    public float getWalkSpeed() {
        return this.getLastAbilitiesPacket().map(PacketPlayServerPlayerAbilities::getWalkSpeed).orElse(-1F);
    }
}
