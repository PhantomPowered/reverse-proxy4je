package de.derklaro.minecraft.proxy.brand;

import de.derrop.minecraft.proxy.api.connection.ProtocolDirection;
import de.derrop.minecraft.proxy.api.event.handler.Listener;
import de.derrop.minecraft.proxy.api.events.PluginMessageReceivedEvent;
import de.derrop.minecraft.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

public final class ProxyBrandChangeListener {

    @Listener
    public void handle(final @NotNull PluginMessageReceivedEvent event) {
        if (event.getPlayer() != null && event.getTag().equals("MC|Brand")) {
            ByteBuf buf = Unpooled.wrappedBuffer(event.getData());
            String serverBrand = ByteBufUtils.readString(buf);
            buf.release();

            buf = Unpooled.buffer();
            ByteBufUtils.writeString(event.getDirection() == ProtocolDirection.TO_SERVER ? "vanilla" : "RopProxy <-> " + serverBrand, buf);
            event.setData(ByteBufUtils.toArray(buf));
        }
    }

}
