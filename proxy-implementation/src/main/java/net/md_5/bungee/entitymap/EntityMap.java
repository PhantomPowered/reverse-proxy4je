package net.md_5.bungee.entitymap;

import com.github.derrop.proxy.util.nbt.CompressedStreamTools;
import com.github.derrop.proxy.util.nbt.NBTSizeTracker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.IOException;

/**
 * Class to rewrite integers within packets.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class EntityMap {

    private final boolean[] clientboundInts = new boolean[256];
    private final boolean[] clientboundVarInts = new boolean[256];

    private final boolean[] serverboundInts = new boolean[256];
    private final boolean[] serverboundVarInts = new boolean[256];

    // Returns the correct entity map for the protocol version
    public static EntityMap getEntityMap(int version) {
        switch (version) {
            case ProtocolConstants.MINECRAFT_1_8:
                return EntityMap_1_8.INSTANCE;
        }
        throw new RuntimeException("Version " + version + " has no entity map");
    }

    protected void addRewrite(int id, ProtocolConstants.Direction direction, boolean varint) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            if (varint) {
                clientboundVarInts[id] = true;
            } else {
                clientboundInts[id] = true;
            }
        } else if (varint) {
            serverboundVarInts[id] = true;
        } else {
            serverboundInts[id] = true;
        }
    }

    public void rewriteServerbound(ByteBuf packet, int oldId, int newId) {
        rewrite(packet, oldId, newId, serverboundInts, serverboundVarInts);
    }

    public void rewriteServerbound(ByteBuf packet, int oldId, int newId, int protocolVersion) {
        rewriteServerbound(packet, oldId, newId);
    }

    public void rewriteClientbound(ByteBuf packet, int oldId, int newId) {
        rewrite(packet, oldId, newId, clientboundInts, clientboundVarInts);
    }

    public void rewriteClientbound(ByteBuf packet, int oldId, int newId, int protocolVersion) {
        rewriteClientbound(packet, oldId, newId);
    }

    protected static void rewriteInt(ByteBuf packet, int oldId, int newId, int offset) {
        int readId = packet.getInt(offset);
        if (readId == oldId) {
            packet.setInt(offset, newId);
        } else if (readId == newId) {
            packet.setInt(offset, oldId);
        }
    }

    protected static void rewriteVarInt(ByteBuf packet, int oldId, int newId, int offset) {
        // Need to rewrite the packet because VarInts are variable length
        int readId = DefinedPacket.readVarInt(packet);
        int readIdLength = packet.readerIndex() - offset;
        if (readId == oldId || readId == newId) {
            ByteBuf data = packet.copy();
            packet.readerIndex(offset);
            packet.writerIndex(offset);
            DefinedPacket.writeVarInt(readId == oldId ? newId : oldId, packet);
            packet.writeBytes(data);
            //data.release();
        }
    }

    protected static void rewriteMetaVarInt(ByteBuf packet, int oldId, int newId, int metaIndex) {
        rewriteMetaVarInt(packet, oldId, newId, metaIndex, -1);
    }

    protected static void rewriteMetaVarInt(ByteBuf packet, int oldId, int newId, int metaIndex, int protocolVersion) {
        int readerIndex = packet.readerIndex();

        short index;
        while ((index = packet.readUnsignedByte()) != 0xFF) {
            int type = DefinedPacket.readVarInt(packet);
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
                switch (type) {
                    case 5: // optional chat
                        if (packet.readBoolean()) {
                            DefinedPacket.readString(packet);
                        }
                        continue;
                    case 15: // particle
                        int particleId = DefinedPacket.readVarInt(packet);

                        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_14) {
                            switch (particleId) {
                                case 3: // minecraft:block
                                case 23: // minecraft:falling_dust
                                    DefinedPacket.readVarInt(packet); // block state
                                    break;
                                case 14: // minecraft:dust
                                    packet.skipBytes(16); // float, float, float, flat
                                    break;
                                case 32: // minecraft:item
                                    readSkipSlot(packet, protocolVersion);
                                    break;
                            }
                        } else {
                            switch (particleId) {
                                case 3: // minecraft:block
                                case 20: // minecraft:falling_dust
                                    DefinedPacket.readVarInt(packet); // block state
                                    break;
                                case 11: // minecraft:dust
                                    packet.skipBytes(16); // float, float, float, flat
                                    break;
                                case 27: // minecraft:item
                                    readSkipSlot(packet, protocolVersion);
                                    break;
                            }
                        }
                        continue;
                    default:
                        if (type >= 6) {
                            type--;
                        }
                        break;
                }
            }

            switch (type) {
                case 0:
                    packet.skipBytes(1); // byte
                    break;
                case 1:
                    if (index == metaIndex) {
                        int position = packet.readerIndex();
                        rewriteVarInt(packet, oldId, newId, position);
                        packet.readerIndex(position);
                    }
                    DefinedPacket.readVarInt(packet);
                    break;
                case 2:
                    packet.skipBytes(4); // float
                    break;
                case 3:
                case 4:
                    DefinedPacket.readString(packet);
                    break;
                case 5:
                    readSkipSlot(packet, protocolVersion);
                    break;
                case 6:
                    packet.skipBytes(1); // boolean
                    break;
                case 7:
                    packet.skipBytes(12); // float, float, float
                    break;
                case 8:
                    packet.readLong();
                    break;
                case 9:
                    if (packet.readBoolean()) {
                        packet.skipBytes(8); // long
                    }
                    break;
                case 10:
                    DefinedPacket.readVarInt(packet);
                    break;
                case 11:
                    if (packet.readBoolean()) {
                        packet.skipBytes(16); // long, long
                    }
                    break;
                case 12:
                    DefinedPacket.readVarInt(packet);
                    break;
                case 13:
                    try {
                        CompressedStreamTools.read(new ByteBufInputStream(packet), new NBTSizeTracker(Integer.MAX_VALUE));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case 15:
                    DefinedPacket.readVarInt(packet);
                    DefinedPacket.readVarInt(packet);
                    DefinedPacket.readVarInt(packet);
                    break;
                case 16:
                    if (index == metaIndex) {
                        int position = packet.readerIndex();
                        rewriteVarInt(packet, oldId + 1, newId + 1, position);
                        packet.readerIndex(position);
                    }
                    DefinedPacket.readVarInt(packet);
                    break;
                case 17:
                    DefinedPacket.readVarInt(packet);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown meta type " + type);
            }
        }

        packet.readerIndex(readerIndex);
    }

    private static void readSkipSlot(ByteBuf packet, int protocolVersion) {
        if ((protocolVersion >= ProtocolConstants.MINECRAFT_1_13_2) ? packet.readBoolean() : packet.readShort() != -1) {
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13_2) {
                DefinedPacket.readVarInt(packet);
            }
            packet.skipBytes((protocolVersion >= ProtocolConstants.MINECRAFT_1_13) ? 1 : 3); // byte vs byte, short

            int position = packet.readerIndex();
            if (packet.readByte() != 0) {
                packet.readerIndex(position);

                try {
                    CompressedStreamTools.read(new ByteBufInputStream(packet), new NBTSizeTracker(Integer.MAX_VALUE));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    // Handles simple packets
    private static void rewrite(ByteBuf packet, int oldId, int newId, boolean[] ints, boolean[] varints) {
        int readerIndex = packet.readerIndex();
        int packetId = DefinedPacket.readVarInt(packet);
        int packetIdLength = packet.readerIndex() - readerIndex;

        if (ints[packetId]) {
            rewriteInt(packet, oldId, newId, readerIndex + packetIdLength);
        } else if (varints[packetId]) {
            rewriteVarInt(packet, oldId, newId, readerIndex + packetIdLength);
        }
        packet.readerIndex(readerIndex);
    }
}
