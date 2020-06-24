package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatRequest;

import java.util.ArrayList;
import java.util.List;

public class LabyPacketLoginRequest extends LabyPacket {
    private List<ChatRequest> requesters;

    public LabyPacketLoginRequest(List<ChatRequest> requesters) {
        this.requesters = requesters;
    }


    public LabyPacketLoginRequest() {
    }


    public List<ChatRequest> getRequests() {
        return this.requesters;
    }


    public void read(ProtoBuf buf) {
        this.requesters = new ArrayList<>();

        int a = buf.readInt();
        for (int i = 0; i < a; i++) {
            this.requesters.add((ChatRequest) LabyBufferUtils.readChatUser(buf));
        }
    }


    public void write(ProtoBuf buf) {
        buf.writeInt(getRequests().size());
        for (int i = 0; i < getRequests().size(); i++) {
            LabyBufferUtils.writeChatUser(buf, getRequests().get(i));
        }
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


