package com.github.derrop.proxy.brand;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

public final class ProxyBrandChangeListener {

    @Listener
    public void handle(final @NotNull PluginMessageEvent event) {
        if (event.getTag().equals("MC|Brand")) {
            ByteBuf buf = Unpooled.wrappedBuffer(event.getData());
            String serverBrand = ByteBufUtils.readString(buf);
            buf.release();

            buf = Unpooled.buffer();
            ByteBufUtils.writeString(event.getDirection() == ProtocolDirection.TO_SERVER ? "vanilla" : "RopProxy <-> " + serverBrand, buf);
            event.setData(ByteBufUtils.toArray(buf));
        }
    }

}
