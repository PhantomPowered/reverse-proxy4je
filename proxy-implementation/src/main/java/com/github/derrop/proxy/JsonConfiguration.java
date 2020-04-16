package com.github.derrop.proxy;

import com.github.derrop.proxy.api.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JsonConfiguration implements Configuration {

    private static final Path PATH = Paths.get("config.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JsonObject jsonObject;

    @Override
    public void load() {
        if (!Files.exists(PATH)) {
            this.jsonObject = new JsonObject();
            this.jsonObject.addProperty("proxyPort", 25565);
            this.jsonObject.addProperty("webPort", 80);

            this.save();
            return;
        }

        try (Reader reader = new InputStreamReader(Files.newInputStream(PATH, StandardOpenOption.CREATE), StandardCharsets.UTF_8)) {
            this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void save() {
        if (this.jsonObject == null) {
            this.load();
            return;
        }

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
}
