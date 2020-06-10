/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.plugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

public final class LabyModListener {

    @Listener
    public void handle(final @NotNull PluginMessageEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_SERVER || !event.getTag().equals("LMC")) {
            return;
        }

        ByteBuf byteBuf = Unpooled.wrappedBuffer(event.getData());
        String messageKey = ByteBufUtils.readString(byteBuf);
        if (messageKey.equals("PERMISSIONS")) {
            event.cancel(true);
            return;
        }
        if (!messageKey.equals("INFO")) {
            return;
        }

        JsonObject content = JsonParser.parseString(ByteBufUtils.readString(byteBuf)).getAsJsonObject();
        content.add("addons", new JsonArray());
        if (content.has("mods") && !content.get("mods").isJsonNull()) {
            content.add("mods", new JsonArray());
        }

        ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeString("INFO", buf);
        ByteBufUtils.writeString(content.toString(), buf);

        event.setData(ByteBufUtils.toArray(buf));
    }
}
