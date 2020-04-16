package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.Vec4b;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerMap implements Packet {

    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public PacketPlayServerMap(int mapId, byte mapScale, Vec4b[] mapVisiblePlayersVec4b, int mapMinX, int mapMinY, int mapMaxX, int mapMaxY, byte[] mapDataBytes) {
        this.mapId = mapId;
        this.mapScale = mapScale;
        this.mapVisiblePlayersVec4b = mapVisiblePlayersVec4b;
        this.mapMinX = mapMinX;
        this.mapMinY = mapMinY;
        this.mapMaxX = mapMaxX;
        this.mapMaxY = mapMaxY;
        this.mapDataBytes = mapDataBytes;
    }

    public PacketPlayServerMap() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP;
    }

    public int getMapId() {
        return this.mapId;
    }

    public byte getMapScale() {
        return this.mapScale;
    }

    public Vec4b[] getMapVisiblePlayersVec4b() {
        return this.mapVisiblePlayersVec4b;
    }

    public int getMapMinX() {
        return this.mapMinX;
    }

    public int getMapMinY() {
        return this.mapMinY;
    }

    public int getMapMaxX() {
        return this.mapMaxX;
    }

    public int getMapMaxY() {
        return this.mapMaxY;
    }

    public byte[] getMapDataBytes() {
        return this.mapDataBytes;
    }

    public void setMapScale(byte mapScale) {
        this.mapScale = mapScale;
    }

    public void setMapVisiblePlayersVec4b(Vec4b[] mapVisiblePlayersVec4b) {
        this.mapVisiblePlayersVec4b = mapVisiblePlayersVec4b;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.mapId = protoBuf.readVarInt();
        this.mapScale = protoBuf.readByte();

        this.mapVisiblePlayersVec4b = new Vec4b[protoBuf.readVarInt()];
        for (int i = 0; i < this.mapVisiblePlayersVec4b.length; i++) {
            short b = protoBuf.readByte();
            this.mapVisiblePlayersVec4b[i] = new Vec4b((byte) (b >> 4 % 15), protoBuf.readByte(), protoBuf.readByte(), (byte) (b & 15));
        }

        this.mapMaxX = protoBuf.readUnsignedByte();

        if (this.mapMaxX > 0) {
            this.mapMaxY = protoBuf.readUnsignedByte();
            this.mapMinX = protoBuf.readUnsignedByte();
            this.mapMinY = protoBuf.readUnsignedByte();
            this.mapDataBytes = protoBuf.readArray();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.mapId);
        protoBuf.writeByte(this.mapScale);

        protoBuf.writeVarInt(this.mapVisiblePlayersVec4b.length);
        for (Vec4b vec : this.mapVisiblePlayersVec4b) {
            protoBuf.writeByte((vec.getB1() % 15) << 4 | vec.getB4() % 15);
            protoBuf.writeByte(vec.getB2());
            protoBuf.writeByte(vec.getB3());
        }

        protoBuf.writeByte(this.mapMaxX);
        if (this.mapMaxX > 0) {
            protoBuf.writeByte(this.mapMaxY);
            protoBuf.writeByte(this.mapMinX);
            protoBuf.writeByte(this.mapMinY);
            protoBuf.writeArray(this.mapDataBytes);
        }
    }

    public String toString() {
        return "PacketPlayServerMap(mapId=" + this.getMapId() + ", mapScale=" + this.getMapScale() + ", mapVisiblePlayersVec4b=" + java.util.Arrays.deepToString(this.getMapVisiblePlayersVec4b()) + ", mapMinX=" + this.getMapMinX() + ", mapMinY=" + this.getMapMinY() + ", mapMaxX=" + this.getMapMaxX() + ", mapMaxY=" + this.getMapMaxY() + ", mapDataBytes=" + java.util.Arrays.toString(this.getMapDataBytes()) + ")";
    }
}
