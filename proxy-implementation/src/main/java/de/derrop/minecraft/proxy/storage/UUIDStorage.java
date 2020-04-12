package de.derrop.minecraft.proxy.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDStorage {

    private static final Path PATH = Paths.get("uuids.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type uuidMapperType = TypeToken.getParameterized(Map.class, UUID.class, String.class).getType();

    private Map<UUID, String> uuidMapper = new HashMap<>();
    private Map<String, UUID> nameMapper = new HashMap<>();

    public UUIDStorage() {
        this.load();
    }

    public void load() {
        if (!Files.exists(PATH)) {
            return;
        }
        try (Reader reader = new InputStreamReader(Files.newInputStream(PATH), StandardCharsets.UTF_8)) {
            Map<UUID, String> uuidMapper = this.gson.fromJson(reader, this.uuidMapperType);

            this.uuidMapper.putAll(uuidMapper);
            this.nameMapper.putAll(uuidMapper.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void save() {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(PATH), StandardCharsets.UTF_8)) {
            this.gson.toJson(this.uuidMapper, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void createMapping(UUID uniqueId, String name) {
        this.uuidMapper.put(uniqueId, name);
        this.nameMapper.put(name, uniqueId);
        this.save();
    }

    public UUID getUniqueId(String name) {
        return this.nameMapper.get(name);
    }

    public String getName(UUID uniqueId) {
        return this.uuidMapper.get(uniqueId);
    }

}
