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
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public byte getMapScale() {
        return mapScale;
    }

    public void setMapScale(byte mapScale) {
        this.mapScale = mapScale;
    }

    public Vec4b[] getMapVisiblePlayersVec4b() {
        return mapVisiblePlayersVec4b;
    }

    public void setMapVisiblePlayersVec4b(Vec4b[] mapVisiblePlayersVec4b) {
        this.mapVisiblePlayersVec4b = mapVisiblePlayersVec4b;
    }

    public int getMapMinX() {
        return mapMinX;
    }

    public void setMapMinX(int mapMinX) {
        this.mapMinX = mapMinX;
    }

    public int getMapMinY() {
        return mapMinY;
    }

    public void setMapMinY(int mapMinY) {
        this.mapMinY = mapMinY;
    }

    public int getMapMaxX() {
        return mapMaxX;
    }

    public void setMapMaxX(int mapMaxX) {
        this.mapMaxX = mapMaxX;
    }

    public int getMapMaxY() {
        return mapMaxY;
    }

    public void setMapMaxY(int mapMaxY) {
        this.mapMaxY = mapMaxY;
    }

    public byte[] getMapDataBytes() {
        return mapDataBytes;
    }

    public void setMapDataBytes(byte[] mapDataBytes) {
        this.mapDataBytes = mapDataBytes;
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
