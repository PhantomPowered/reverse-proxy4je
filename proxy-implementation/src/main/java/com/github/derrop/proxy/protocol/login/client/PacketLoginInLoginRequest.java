/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.derrop.proxy.protocol.login.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginInLoginRequest implements Packet {

    private String data;

    public PacketLoginInLoginRequest(String data) {
        this.data = data;
    }

    public PacketLoginInLoginRequest() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Login.START;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.data = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.data);
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return "PacketLoginInLoginRequest(data=" + this.getData() + ")";
    }
}
