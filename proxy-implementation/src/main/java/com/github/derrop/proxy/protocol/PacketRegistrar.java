package com.github.derrop.proxy.protocol;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingInSetProtocol;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInLoginRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutServerKickPlayer;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutPong;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInPing;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInRequest;

public final class PacketRegistrar {

    private PacketRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void registerPackets() {
        PacketRegistry registry = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PacketRegistry.class);

        // Handshake
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.HANDSHAKING, new PacketHandshakingInSetProtocol());

        // Login
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginInLoginRequest());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.LOGIN, new PacketLoginInEncryptionRequest());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutEncryptionResponse());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutLoginSuccess());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutServerKickPlayer());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.LOGIN, new PacketLoginOutSetCompression());

        // Status
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInPing());
        registry.registerPacket(ProtocolDirection.TO_SERVER, ProtocolState.STATUS, new PacketStatusInRequest());

        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutPong());
        registry.registerPacket(ProtocolDirection.TO_CLIENT, ProtocolState.STATUS, new PacketStatusOutResponse());

        // Play

    }
}
