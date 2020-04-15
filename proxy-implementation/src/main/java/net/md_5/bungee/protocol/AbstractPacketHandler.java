package net.md_5.bungee.protocol;

import com.github.derrop.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPacketHandler {

    public abstract void handle(@NotNull Packet packet) throws Exception;

}
