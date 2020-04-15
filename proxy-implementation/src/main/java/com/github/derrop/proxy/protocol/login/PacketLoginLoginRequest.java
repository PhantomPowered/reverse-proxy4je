/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.derrop.proxy.protocol.login;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketLoginLoginRequest extends DefinedPacket {

    private String data;

    @Override
    public void read(@NotNull ByteBuf buf) {
        data = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(data, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Login.START;
    }
}
