package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
public class PacketPlayCustomPayload extends PacketPlayPluginMessage {
    public PacketPlayCustomPayload(String tag, byte[] data, boolean allowExtendedPacket) {
        super(tag, data, allowExtendedPacket);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.CUSTOM_PAYLOAD;
    }
}
