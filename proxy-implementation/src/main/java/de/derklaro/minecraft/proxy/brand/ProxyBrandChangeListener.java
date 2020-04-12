package de.derklaro.minecraft.proxy.brand;

import de.derrop.minecraft.proxy.api.event.handler.Listener;
import de.derrop.minecraft.proxy.api.events.PluginMessageReceivedEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

public final class ProxyBrandChangeListener {

    @Listener
    public void handle(final @NotNull PluginMessageReceivedEvent event) {
        if (event.getPlayer() != null && event.getTag().equals("MC|Brand")) {
            ByteBuf brand = Unpooled.wrappedBuffer(event.getData());
            String serverBrand = DefinedPacket.readString(brand);

            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString("RopProxy <- " + serverBrand, brand);
            event.setData(DefinedPacket.toArray(brand)); // TODO just use the "vanilla" brand
        }
    }

}
