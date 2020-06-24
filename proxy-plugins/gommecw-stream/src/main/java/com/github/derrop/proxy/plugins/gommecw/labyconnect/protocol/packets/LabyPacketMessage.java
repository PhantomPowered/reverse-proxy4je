package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketMessage extends LabyPacket {
    private ChatUser sender;
    private ChatUser to;
    private String message;
    private long sentTime;
    private long fileSize;
    private double audioTime;

    public LabyPacketMessage(ChatUser sender, ChatUser to, String message, long fileSize, double time, long sentTime) {
        this.sender = sender;
        this.to = to;
        this.message = message;
        this.fileSize = fileSize;
        this.audioTime = time;
        this.sentTime = sentTime;
    }


    public LabyPacketMessage() {
    }


    public void read(ProtoBuf buf) {
        this.sender = LabyBufferUtils.readChatUser(buf);
        this.to = LabyBufferUtils.readChatUser(buf);
        this.message = LabyBufferUtils.readString(buf);
        this.fileSize = buf.readLong();
        this.audioTime = buf.readDouble();
        this.sentTime = buf.readLong();
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.sender);
        LabyBufferUtils.writeChatUser(buf, this.to);
        LabyBufferUtils.writeString(buf, this.message);
        buf.writeLong(this.fileSize);
        buf.writeDouble(this.audioTime);
        buf.writeLong(this.sentTime);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public double getAudioTime() {
        return this.audioTime;
    }

    public long getFileSize() {
        return this.fileSize;
    }


    public String getMessage() {
        return this.message;
    }


    public ChatUser getSender() {
        return this.sender;
    }


    public ChatUser getTo() {
        return this.to;
    }


    public long getSentTime() {
        return this.sentTime;
    }
}


