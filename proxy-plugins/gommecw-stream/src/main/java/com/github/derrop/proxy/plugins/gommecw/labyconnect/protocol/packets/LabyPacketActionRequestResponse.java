package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.util.UUID;


public class LabyPacketActionRequestResponse extends LabyPacket {
    private UUID uuid;
    private short actionId;
    private byte[] data;

    public LabyPacketActionRequestResponse() {
    }

    public LabyPacketActionRequestResponse(UUID uuid, short actionId, byte[] data) {
        this.uuid = uuid;
        this.actionId = actionId;
        this.data = data;
    }

    public short getActionId() {
        return this.actionId;
    }

    public byte[] getData() {
        return this.data;
    }

    public void read(ProtoBuf buf) {
        this.uuid = UUID.fromString(LabyBufferUtils.readString(buf));
        this.actionId = buf.readShort();

        int length = buf.readVarInt();

        if (length > 1024) {
            throw new RuntimeException("data array too big");
        }
        this.data = new byte[length];
        buf.readBytes(this.data);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.uuid.toString());
        buf.writeShort(this.actionId);

        if (this.data == null) {
            buf.writeVarInt(0);
        } else {
            buf.writeVarInt(this.data.length);
            buf.writeBytes(this.data);
        }
    }

    public void handle(PacketHandler handler) {
     /*User user;
     switch (this.actionId) {

       case 1:
         if (LabyModCore.getMinecraft().getPlayer() != null && LabyModCore.getMinecraft().getPlayer().aK().equals(this.uuid)) {
           break;
         }
         LabyMod.getInstance().getEmoteRegistry().handleEmote(this.uuid, this.data);
         break;


       case 2:
         LabyMod.getInstance().getUserManager().updateUsersJson(this.uuid, new String(this.data, StandardCharsets.UTF_8), null);
         break;

       case 3:
         if (LabyModCore.getMinecraft().getPlayer() != null && LabyModCore.getMinecraft().getPlayer().aK().equals(this.uuid)) {
           break;
         }
         user = LabyMod.getInstance().getUserManager().getUser(this.uuid);
         LabyMod.getInstance().getStickerRegistry().handleSticker(user, LabyMod.getInstance().getStickerRegistry().bytesToShort(this.data));
         break;
     }*/
    }

    public UUID getUuid() {
        return this.uuid;
    }
}


