package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.connection.packet.Packet;
import org.jetbrains.annotations.NotNull;

public interface PacketSender {

    void sendPacket(@NotNull Packet packet);

}
