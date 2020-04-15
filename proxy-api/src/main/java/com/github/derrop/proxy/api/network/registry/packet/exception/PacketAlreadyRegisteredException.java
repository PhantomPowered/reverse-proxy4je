package com.github.derrop.proxy.api.network.registry.packet.exception;

import com.github.derrop.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;

public class PacketAlreadyRegisteredException extends RuntimeException {

    private static final long serialVersionUID = -6244586836054499616L;

    public PacketAlreadyRegisteredException(@NotNull Packet packet) {
        super("The packet " + packet.getClass().getName() + "@" + packet.getId() + " is already registered");
    }
}
