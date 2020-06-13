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
package com.github.derrop.proxy.config;

import com.github.derrop.proxy.api.Configuration;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JsonConfiguration implements Configuration {

    private static final Path PATH = Paths.get("config.json");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Component.class, new LegacyGsonComponentSerializer())
            .setPrettyPrinting()
            .create();

    private JsonObject jsonObject;

    private ServerPing motd;

    @Override
    public void load() {
        if (!Files.exists(PATH)) {
            this.jsonObject = new JsonObject();
            this.jsonObject.addProperty("proxyPort", 25565);
            this.jsonObject.addProperty("webPort", 80);
            this.motd = new ServerPing(
                    new ServerPing.Protocol("§cProxy by §bderrop §cand §bderklaro", -1),
                    new ServerPing.Players(0, 0, null),
                    TextComponent.of("\n§7Available/Online Accounts: §e$free§7/§e$online"),
                    null
            );

            this.save();
            return;
        }

        try (Reader reader = new InputStreamReader(Files.newInputStream(PATH, StandardOpenOption.CREATE), StandardCharsets.UTF_8)) {
            this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.motd = Utils.GSON.fromJson(this.jsonObject.get("motd"), ServerPing.class);
        // TODO load server-icon.png
    }

    @Override
    public void save() {
        if (this.jsonObject == null) {
            this.load();
            return;
        }

        this.jsonObject.add("motd", this.gson.toJsonTree(this.motd));

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(PATH, StandardOpenOption.CREATE), StandardCharsets.UTF_8)) {
            this.gson.toJson(this.jsonObject, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int getProxyPort() {
        return this.jsonObject.get("proxyPort").getAsInt();
    }

    @Override
    public void setProxyPort(int proxyPort) {
        this.jsonObject.addProperty("proxyPort", proxyPort);
    }

    @Override
    public int getWebPort() {
        return this.jsonObject.get("webPort").getAsInt();
    }

    @Override
    public void setWebPort(int webPort) {
        this.jsonObject.addProperty("webPort", webPort);
    }

    @Override
    public ServerPing getMotd() {
        return this.motd.clone();
    }

    @Override
    public void setMotd(ServerPing motd) {
        this.motd = motd;
    }
}
