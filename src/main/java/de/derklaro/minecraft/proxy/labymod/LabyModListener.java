package de.derklaro.minecraft.proxy.labymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.derklaro.minecraft.proxy.event.handler.Listener;
import de.derklaro.minecraft.proxy.events.PluginMessageReceivedEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

public final class LabyModListener {

    @Listener
    public void handle(final @NotNull PluginMessageReceivedEvent event) {
        if (!event.getPluginMessage().getTag().equals("LMC")) {
            return;
        }

        ByteBuf byteBuf = Unpooled.wrappedBuffer(event.getPluginMessage().getData());
        String messageKey = DefinedPacket.readString(byteBuf);
        if (!messageKey.equals("INFO") || !event.getDirection().equals(ProtocolConstants.Direction.TO_SERVER)) {
            return;
        }

        JsonObject content = JsonParser.parseString(DefinedPacket.readString(byteBuf)).getAsJsonObject();
        content.add("addons", new JsonArray());

        ByteBuf buf = Unpooled.buffer();
        DefinedPacket.writeString("INFO", buf);
        DefinedPacket.writeString(content.toString(), buf);

        event.getPluginMessage().setData(DefinedPacket.toArray(buf));
    }
}
