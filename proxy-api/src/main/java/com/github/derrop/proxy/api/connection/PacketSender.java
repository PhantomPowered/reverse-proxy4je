package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.connection.packet.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public interface PacketSender {

    void sendPacket(@NotNull Packet packet);

    void sendPacket(@NotNull ByteBuf byteBuf);

    @NotNull
    NetworkUnsafe networkUnsafe();

    interface NetworkUnsafe {

        void sendPacket(@NotNull Object packet);

    }

}
