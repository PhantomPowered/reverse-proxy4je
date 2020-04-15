package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayCustomPayload extends PacketPlayPluginMessage {

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Play.CUSTOM_PAYLOAD;
    }
}
