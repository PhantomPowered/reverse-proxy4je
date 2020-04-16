package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class GommeStatsListener {

    @Listener
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || !event.getTag().equals("GoMod")) {
            return;
        }

        ByteBuf buf = Unpooled.wrappedBuffer(event.getData());

        JsonObject jsonObject = JsonParser.parseString(ByteBufUtils.readString(buf)).getAsJsonObject();

        String action = jsonObject.get("action").getAsString().toUpperCase();
        JsonObject data = jsonObject.has("data") ? jsonObject.get("data").getAsJsonObject() : null;

        if (data != null && action.equals("JOIN_SERVER")) {
            String serverType = data.get("cloud_type").getAsString().toUpperCase();
            String matchId = data.get("id").getAsString();

            // TODO
        }
    }

}
