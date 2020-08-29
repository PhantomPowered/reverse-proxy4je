/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.protocol;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistry;
import com.github.phantompowered.proxy.protocol.handshake.PacketHandshakingClientSetProtocol;
import com.github.phantompowered.proxy.protocol.login.client.PacketLoginClientLoginRequest;
import com.github.phantompowered.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutServerKickPlayer;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.phantompowered.proxy.protocol.play.client.*;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientEntityAction;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientSteerVehicle;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientUseEntity;
import com.github.phantompowered.proxy.protocol.play.client.inventory.*;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientLook;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPosition;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPositionLook;
import com.github.phantompowered.proxy.protocol.play.server.*;
import com.github.phantompowered.proxy.protocol.play.server.entity.*;
import com.github.phantompowered.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;
import com.github.phantompowered.proxy.protocol.play.server.entity.effect.PacketPlayServerRemoveEntityEffect;
import com.github.phantompowered.proxy.protocol.play.server.entity.effect.PacketPlayServerUpdateEntityAttributes;
import com.github.phantompowered.proxy.protocol.play.server.entity.position.*;
import com.github.phantompowered.proxy.protocol.play.server.entity.spawn.*;
import com.github.phantompowered.proxy.protocol.play.server.inventory.*;
import com.github.phantompowered.proxy.protocol.play.server.message.*;
import com.github.phantompowered.proxy.protocol.play.server.player.*;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerSpawnPosition;
import com.github.phantompowered.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardDisplay;
import com.github.phantompowered.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardObjective;
import com.github.phantompowered.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardScore;
import com.github.phantompowered.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardTeam;
import com.github.phantompowered.proxy.protocol.play.server.world.*;
import com.github.phantompowered.proxy.protocol.play.server.world.effect.*;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMapChunk;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMapChunkBulk;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerMultiBlockChange;
import com.github.phantompowered.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.phantompowered.proxy.protocol.status.client.PacketStatusOutPong;
import com.github.phantompowered.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.phantompowered.proxy.protocol.status.server.PacketStatusInPing;
import com.github.phantompowered.proxy.protocol.status.server.PacketStatusInRequest;

public final class PacketRegistrar {

    private PacketRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void registerPackets(PacketRegistry registry) {
        // Handshake
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.HANDSHAKING, new PacketHandshakingClientSetProtocol());

        // Login
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginClientLoginRequest());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginOutEncryptionResponse());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginInEncryptionRequest());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutLoginSuccess());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutServerKickPlayer());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutSetCompression());

        // Status
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInPing());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInRequest());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutPong());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutResponse());

        // Play
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerChatMessage());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientChatMessage());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientResourcePackStatusResponse());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientSettings());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientTabCompleteRequest());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientCustomPayload());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPluginMessage());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientPlayerDigging());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientUseEntity());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientArmAnimation());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientBlockPlace());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientEntityAction());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientSteerVehicle());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientSpectate());
        // Keep alive
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayKeepAlive());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayKeepAlive());
        // Effect
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityEffect());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerRemoveEntityEffect());
        // Player
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerCamera());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerGameStateChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPlayerAbilities());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientPlayerAbilities());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateHealth());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerHeldItemSlot());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientHeldItemSlot());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSetExperience());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerOpenSignEditor());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPlayerStatistics());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayPlayerCombatEvent());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientCommand());
        // Player movement
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientPlayerPosition());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientPosition());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientLook());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientPositionLook());
        // Spawn
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerNamedEntitySpawn());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntityWeather());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnLivingEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnPosition());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerPosition());
        // Entity
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityDestroy());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityMetadata());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityStatus());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityTeleport());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityEquipment());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityUseBed());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityAnimation());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntityPainting());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSpawnEntityExperienceOrb());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityVelocity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityCollectItem());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityRelMove());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityLook());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityLookMove());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityHeadRotation());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerEntityAttach());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateEntityAttributes());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateTileEntity());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateEntityNBT());
        // Inventory
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSetSlot());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWindowItems());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerCloseWindow());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerOpenWindow());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWindowProgressBar());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerConfirmTransaction());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientCloseWindow());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientClickWindow());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientConfirmTransaction());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientSetCreativeItem());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientEnchantItem());
        // World
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerBlockChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMap());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMapChunk());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMapChunkBulk());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerMultiBlockChange());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerTimeUpdate());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerUpdateSign());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.PLAY, new PacketPlayClientUpdateSign());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWorldBorder());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerBlockAction());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWorldSound());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSound());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerExplosion());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerBlockBreakAnimation());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerWorldParticles());
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
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSetCompression());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.PLAY, new PacketPlayServerSetDifficulty());
    }
}
