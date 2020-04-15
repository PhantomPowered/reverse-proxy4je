package net.md_5.bungee.protocol;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerResourcePackSend;
import com.github.derrop.proxy.protocol.play.server.PacketPlayClientResourcePackStatusResponse;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerDestroyEntities;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
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
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityStatus;
import com.github.derrop.proxy.protocol.play.server.world.*;
import com.github.derrop.proxy.connection.velocity.*;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingInSetProtocol;
import com.github.derrop.proxy.protocol.login.*;
import com.github.derrop.proxy.protocol.play.client.*;
import com.github.derrop.proxy.protocol.play.server.*;
import com.github.derrop.proxy.protocol.play.shared.*;
import com.github.derrop.proxy.protocol.status.PacketStatusPing;
import com.github.derrop.proxy.protocol.status.PacketStatusRequest;
import com.github.derrop.proxy.protocol.status.PacketStatusResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Constructor;

public enum Protocol {

    // Undef
    HANDSHAKE {

        {
            TO_SERVER.registerPacket(
                    PacketHandshakingInSetProtocol.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00)
            );
        }
    },
    // 0
    GAME {

        {
            TO_CLIENT.registerPacket(
                    PacketPlayKeepAlive.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x21)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerLogin.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x25),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x26)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayChatMessage.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x0F)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerRespawn.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x07),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x33),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x34),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x35),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x3B)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerPlayerListItem.class, // PlayerInfo
                    map(ProtocolConstants.MINECRAFT_1_8, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x2D),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x2E),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x30),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x33),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x34)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerTabCompleteResponse.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x11)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerScoreboardObjective.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4A)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerScoreboardScore.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4D)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerScoreboardDisplay.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x43)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerScoreboardTeam.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x43),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4C)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayPluginMessage.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x19)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerKickPlayer.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x40),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1B)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerTitle.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x50)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerPlayerListHeaderFooter.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_9_4, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x4E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x54)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerEntityStatus.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1C)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerChunkData.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.CHUNK_DATA)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerChunkBulk.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.CHUNK_BULK)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerWindowItems.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.WINDOW_ITEMS)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSetSlot.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.SET_SLOT)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSpawnPlayer.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.SPAWN_PLAYER)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerBlockUpdate.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.BLOCK_UPDATE)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerMultiBlockUpdate.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.MULTI_BLOCK_UPDATE)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerPlayerAbilities.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.PLAYER_ABILITIES)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerEntityTeleport.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.ENTITY_TELEPORT)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSpawnGlobalEntity.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.GLOBAL_ENTITY_SPAWN)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSpawnObject.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.SPAWN_OBJECT)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSpawnMob.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.SPAWN_MOB)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerEntityMetadata.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.ENTITY_METADATA)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerDestroyEntities.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.DESTROY_ENTITIES)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerWorldBorder.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.WORLD_BORDER)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerEntityEffect.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.ENTITY_EFFECT)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerRemoveEntityEffect.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.REMOVE_ENTITY_EFFECT)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerCamera.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.CAMERA)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerUpdateSign.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.UPDATE_SIGN)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerMaps.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.MAPS)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerTimeUpdate.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.TIME_UPDATE)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerGameStateChange.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.GAME_STATE_CHANGE)
            );
            TO_CLIENT.registerPacket(
                    EntityVelocity.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 18)
            );
            TO_CLIENT.registerPacket(
                    EntityAttach.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 27)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerSpawnPosition.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 5)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerUpdateHealth.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.UPDATE_HEALTH)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerResourcePackSend.class,
                    map(ProtocolConstants.MINECRAFT_1_8, PacketConstants.RESOURCE_PACK_SEND)
            );

            TO_SERVER.registerPacket(
                    PacketPlayKeepAlive.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x0F)
            );
            TO_SERVER.registerPacket(
                    PacketPlayChatMessage.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x03)
            );
            TO_SERVER.registerPacket(
                    PacketPlayClientTabCompleteRequest.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x14),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x06)
            );
            TO_SERVER.registerPacket(
                    PacketPlayClientSettings.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x15),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x05)
            );
            TO_SERVER.registerPacket(
                    PacketPlayPluginMessage.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x0B)
            );
            TO_SERVER.registerPacket(
                    Player.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 3)
            );
            TO_SERVER.registerPacket(
                    PlayerPosition.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 4)
            );
            TO_SERVER.registerPacket(
                    PlayerLook.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 5)
            );
            TO_SERVER.registerPacket(
                    PacketPlayInPositionLook.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 6)
            );
            TO_SERVER.registerPacket(
                    PacketPlayClientResourcePackStatusResponse.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 25)
            );
        }
    },
    // 1
    STATUS {

        {
            TO_CLIENT.registerPacket(
                    PacketStatusResponse.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00)
            );
            TO_CLIENT.registerPacket(
                    PacketStatusPing.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01)
            );

            TO_SERVER.registerPacket(
                    PacketStatusRequest.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00)
            );
            TO_SERVER.registerPacket(
                    PacketStatusPing.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01)
            );
        }
    },
    //2
    LOGIN {

        {
            TO_CLIENT.registerPacket(
                    PacketPlayServerKickPlayer.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00)
            );
            TO_CLIENT.registerPacket(
                    PacketLoginEncryptionRequest.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01)
            );
            TO_CLIENT.registerPacket(
                    PacketPlayServerLoginSuccess.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x02)
            );
            TO_CLIENT.registerPacket(
                    PacketLoginSetCompression.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x03)
            );

            TO_SERVER.registerPacket(
                    PacketLoginLoginRequest.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00)
            );
            TO_SERVER.registerPacket(
                    PacketLoginEncryptionResponse.class,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01)
            );
        }
    };
    /*========================================================================*/
    public static final int MAX_PACKET_ID = 0xFF;
    /*========================================================================*/
    public final DirectionData TO_SERVER = new DirectionData(this, ProtocolConstants.Direction.TO_SERVER);
    public final DirectionData TO_CLIENT = new DirectionData(this, ProtocolConstants.Direction.TO_CLIENT);

    public static void main(String[] args) {
        for (int version : ProtocolConstants.SUPPORTED_VERSION_IDS) {
            dump(version);
        }
    }

    private static void dump(int version) {
        for (Protocol protocol : Protocol.values()) {
            dump(version, protocol);
        }
    }

    private static void dump(int version, Protocol protocol) {
        dump(version, protocol.TO_CLIENT);
        dump(version, protocol.TO_SERVER);
    }

    private static void dump(int version, DirectionData data) {
        for (int id = 0; id < MAX_PACKET_ID; id++) {
            DefinedPacket packet = data.createPacket(id, version);
            if (packet != null) {
                System.out.println(version + " " + data.protocolPhase + " " + data.direction + " " + id + " " + packet.getClass().getSimpleName());
            }
        }
    }

    @Data
    private static class ProtocolData {

        private final int protocolVersion;
        private final TObjectIntMap<Class<? extends DefinedPacket>> packetMap = new TObjectIntHashMap<>(MAX_PACKET_ID);
        private final Constructor<? extends DefinedPacket>[] packetConstructors = new Constructor[MAX_PACKET_ID];
    }

    @Data
    private static class ProtocolMapping {

        private final int protocolVersion;
        private final int packetID;
    }

    // Helper method
    private static ProtocolMapping map(int protocol, int id) {
        return new ProtocolMapping(protocol, id);
    }

    public static final class DirectionData {

        private final TIntObjectMap<ProtocolData> protocols = new TIntObjectHashMap<>();
        //
        private final Protocol protocolPhase;
        @Getter
        private final ProtocolConstants.Direction direction;

        public DirectionData(Protocol protocolPhase, ProtocolConstants.Direction direction) {
            this.protocolPhase = protocolPhase;
            this.direction = direction;

            for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                protocols.put(protocol, new ProtocolData(protocol));
            }
        }

        private ProtocolData getProtocolData(int version) {
            ProtocolData protocol = protocols.get(version);
            if (protocol == null && (protocolPhase != Protocol.GAME)) {
                protocol = Iterables.getFirst(protocols.valueCollection(), null);
            }
            return protocol;
        }

        public final DefinedPacket createPacket(int id, int version) {
            ProtocolData protocolData = getProtocolData(version);
            if (protocolData == null) {
                throw new BadPacketException("Unsupported net.md_5.bungee.protocol version " + version);
            }
            if (id > MAX_PACKET_ID) {
                throw new BadPacketException("Packet with id " + id + " outside of range ");
            }

            Constructor<? extends DefinedPacket> constructor = protocolData.packetConstructors[id];
            try {
                return (constructor == null) ? null : constructor.newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new BadPacketException("Could not construct packet with id " + id, ex);
            }
        }

        private void registerPacket(Class<? extends DefinedPacket> packetClass, ProtocolMapping... mappings) {
            try {
                Constructor<? extends DefinedPacket> constructor = packetClass.getDeclaredConstructor();

                int mappingIndex = 0;
                ProtocolMapping mapping = mappings[mappingIndex];
                for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                    if (protocol < mapping.protocolVersion) {
                        // This is a new packet, skip it till we reach the next net.md_5.bungee.protocol
                        continue;
                    }

                    if (mapping.protocolVersion < protocol && mappingIndex + 1 < mappings.length) {
                        // Mapping is non current, but the next one may be ok
                        ProtocolMapping nextMapping = mappings[mappingIndex + 1];
                        if (nextMapping.protocolVersion == protocol) {
                            Preconditions.checkState(nextMapping.packetID != mapping.packetID, "Duplicate packet mapping (%s, %s)", mapping.protocolVersion, nextMapping.protocolVersion);

                            mapping = nextMapping;
                            mappingIndex++;
                        }
                    }

                    ProtocolData data = protocols.get(protocol);
                    data.packetMap.put(packetClass, mapping.packetID);
                    data.packetConstructors[mapping.packetID] = constructor;
                }
            } catch (NoSuchMethodException ex) {
                throw new BadPacketException("No NoArgsConstructor for packet class " + packetClass);
            }
        }

        public final int getId(Class<? extends DefinedPacket> packet, int version) {

            ProtocolData protocolData = getProtocolData(version);
            if (protocolData == null) {
                throw new BadPacketException("Unsupported net.md_5.bungee.protocol version");
            }
            Preconditions.checkArgument(protocolData.packetMap.containsKey(packet), "Cannot get ID for packet %s in phase %s with direction %s", packet, protocolPhase, direction);

            return protocolData.packetMap.get(packet);
        }
    }
}
