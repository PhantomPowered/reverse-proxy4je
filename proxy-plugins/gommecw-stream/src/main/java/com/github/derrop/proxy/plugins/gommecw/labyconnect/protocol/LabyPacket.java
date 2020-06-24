package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;

public abstract class LabyPacket {
    public abstract void read(ProtoBuf buf);

    public abstract void write(ProtoBuf buf);

    public abstract void handle(PacketHandler handler);
}


