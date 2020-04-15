package com.github.derrop.proxy.protocol;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingInSetProtocol;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInLoginRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutServerKickPlayer;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.derrop.proxy.protocol.play.client.*;
import com.github.derrop.proxy.protocol.play.server.*;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityDestroy;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityStatus;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerRemoveEntityEffect;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerCamera;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerGameStateChange;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerPlayerAbilities;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerUpdateHealth;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.*;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerWindowItems;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardDisplay;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardObjective;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardScore;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardTeam;
import com.github.derrop.proxy.protocol.play.server.world.*;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutPong;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInPing;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInRequest;

public final class PacketRegistrar {

    private PacketRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void registerPackets() {
        PacketRegistry registry = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PacketRegistry.class);

        // Handshake
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.HANDSHAKING, new PacketHandshakingInSetProtocol());

        // Login
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginInLoginRequest());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginInEncryptionRequest());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutEncryptionResponse());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutLoginSuccess());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutServerKickPlayer());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutSetCompression());

        // Status
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInPing());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInRequest());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutPong());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutResponse());

        // Play
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayChatMessage());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientResourcePackStatusResponse());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientSettings());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientTabCompleteRequest());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayCustomPayload());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayInPositionLook());
        // Effect
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityEffect());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerRemoveEntityEffect());
        // Player
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerCamera());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerGameStateChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPlayerAbilities());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateHealth());
        // Spawn
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerNamedEntitySpawn());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntityWeather());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnLivingEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnPosition());
        // Entity
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityDestroy());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityMetadata());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityStatus());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityTeleport());
        // Inventory
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSetSlot());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWindowItems());
        // World
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerBlockChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMap());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMapChunk());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMapChunkBulk());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMultiBlockChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerTimeUpdate());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateSign());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWorldBorder());
        // Scoreboard
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerScoreboardDisplay());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerScoreboardObjective());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerScoreboardScore());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerScoreboardTeam());
        // Misc
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerKickPlayer());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerLogin());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPlayerInfo());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPlayerListHeaderFooter());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerResourcePackSend());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerRespawn());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerTabCompleteResponse());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerTitle());
    }
}
