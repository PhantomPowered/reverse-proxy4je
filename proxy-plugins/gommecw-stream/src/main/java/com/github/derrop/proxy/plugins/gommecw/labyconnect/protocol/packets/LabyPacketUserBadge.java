package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.util.Arrays;
import java.util.UUID;


public class LabyPacketUserBadge extends LabyPacket {
    private UUID[] uuids;
    private byte[] ranks;

    public LabyPacketUserBadge() {
    }

    public LabyPacketUserBadge(UUID[] uuids) {
        this.uuids = uuids;
    }

    public UUID[] getUuids() {
        return this.uuids;
    }

    public byte[] getRanks() {
        return this.ranks;
    }

    public void read(ProtoBuf buf) {
        int size = buf.readVarInt();


        this.uuids = new UUID[size];
        for (int i = 0; i < size; i++) {
            this.uuids[i] = new UUID(buf.readLong(), buf.readLong());
        }


        byte[] bytes = new byte[size];
        buf.readBytes(bytes);
        this.ranks = bytes;
    }


    public void write(ProtoBuf buf) {
        buf.writeVarInt(this.uuids.length);
        for (int i = 0; i < this.uuids.length; i++) {
            UUID uuid = this.uuids[i];
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
        }
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "PacketUserBadge{"
                + "uuids=" + Arrays.toString(uuids)
                + ", ranks=" + Arrays.toString(ranks)
                + "} " + super.toString();
    }
}


