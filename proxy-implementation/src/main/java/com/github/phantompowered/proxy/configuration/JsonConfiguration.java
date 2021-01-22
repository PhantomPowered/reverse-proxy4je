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
package com.github.phantompowered.proxy.configuration;

import com.github.phantompowered.proxy.ImplementationUtil;
import com.github.phantompowered.proxy.api.configuration.Configuration;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.ping.Favicon;
import com.github.phantompowered.proxy.api.ping.ServerPing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JsonConfiguration implements Configuration {

    private static final Path PATH = Paths.get("config.json");
    private static final Path SERVER_ICON = Paths.get("server-icon.png");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Component.class, new LegacyGsonComponentSerializer())
            .setPrettyPrinting()
            .create();

    private JsonObject jsonObject;
    private ServerPing motd;
    private NetworkAddress targetPingAddress;

    @Override
    public void load() {
        if (!Files.exists(PATH)) {
            this.jsonObject = new JsonObject();
            this.jsonObject.addProperty("proxyPort", 25565);
            this.jsonObject.addProperty("webPort", 80);
            this.jsonObject.addProperty("compression", -1);
            this.jsonObject.addProperty("privateMode", false);
            this.jsonObject.add("targetPingAddress", JsonNull.INSTANCE);
            this.motd = new ServerPing(
                    new ServerPing.Protocol("§6P§7hantom§6P§7roxy §7by §ederklaro§7, §ederrop", -1),
                    new ServerPing.Players(0, 0, null),
                    Component.text("\n§7Available/Online Accounts: §e$free§7/§e$online"),
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

        JsonElement rawAddress = this.jsonObject.get("targetPingAddress");
        this.targetPingAddress = rawAddress == null || rawAddress == JsonNull.INSTANCE ? null : NetworkAddress.parse(rawAddress.getAsString());

        this.motd = ImplementationUtil.GSON.fromJson(this.jsonObject.get("motd"), ServerPing.class);
        if (Files.exists(SERVER_ICON)) {
            try (InputStream inputStream = Files.newInputStream(SERVER_ICON)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image.getHeight() != 64 || image.getWidth() != 64) {
                    throw new IllegalArgumentException("ServerIcon doesn't have the size of 64x64");
                }

                this.motd.setFavicon(Favicon.create(image));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (this.motd.getFavicon() == null) {
            try (InputStream inputStream = JsonConfiguration.class.getClassLoader().getResourceAsStream("server-icon.png")) {
                if (inputStream != null) {
                    this.motd.setFavicon(Favicon.create(ImageIO.read(inputStream)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    public NetworkAddress getMotdTargetAddress() {
        return this.targetPingAddress;
    }

    @Override
    public void setMotdTargetAddress(NetworkAddress address) {
        this.jsonObject.addProperty("targetPingAddress", address.getRawHost() + "@" + address.getPort());
        this.targetPingAddress = address;
    }

    @Override
    public ServerPing getMotd() {
        return this.motd.clone();
    }

    @Override
    public void setMotd(ServerPing motd) {
        this.motd = motd;
    }

    @Override
    public boolean isPrivateMode() {
        return this.jsonObject != null && this.jsonObject.get("privateMode").getAsBoolean();
    }

    @Override
    public void setPrivateMode(boolean privateMode) {
        this.jsonObject.addProperty("privateMode", privateMode);
    }

    @Override
    public @Range(from = 0, to = 256) int getCompressionThreshold() {
        return Math.min(256, this.jsonObject.get("compression").getAsInt());
    }

    @Override
    public void setCompressionThreshold(@Range(from = 0, to = 256) int threshold) {
        this.jsonObject.addProperty("compression", threshold);
    }
}
