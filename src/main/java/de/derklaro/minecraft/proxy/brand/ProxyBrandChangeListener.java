package de.derklaro.minecraft.proxy.brand;

import de.derklaro.minecraft.proxy.event.handler.Listener;
import de.derklaro.minecraft.proxy.events.PluginMessageReceivedEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

public final class ProxyBrandChangeListener {

    @Listener
    public void handle(final @NotNull PluginMessageReceivedEvent event) {
        if (event.getConnection() != null && event.getPluginMessage().getTag().equals("MC|Brand")) {
            ByteBuf brand = Unpooled.wrappedBuffer(event.getPluginMessage().getData());
            String serverBrand = DefinedPacket.readString(brand);

            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString("RopProxy <- " + serverBrand, brand);
            event.getPluginMessage().setData(DefinedPacket.toArray(brand));
        }
    }

}
