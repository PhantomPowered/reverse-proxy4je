package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.player.PacketPlayServerPlayerAbilities;

public class PacketPlayClientPlayerAbilities extends PacketPlayServerPlayerAbilities implements Packet {

    public PacketPlayClientPlayerAbilities(boolean invulnerable, boolean flying, boolean allowFlying, boolean creativeMode, float flySpeed, float walkSpeed) {
        super(invulnerable, flying, allowFlying, creativeMode, flySpeed, walkSpeed);
    }

    public PacketPlayClientPlayerAbilities() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.ABILITIES;
    }

    @Override
    public Packet mapToServerside() {
        return new PacketPlayServerPlayerAbilities(
                super.isInvulnerable(),
                super.isFlying(),
                super.isAllowFlying(),
                super.isCreativeMode(),
                super.getFlySpeed(),
                super.getWalkSpeed()
        );
    }
}
