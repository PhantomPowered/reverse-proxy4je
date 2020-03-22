package de.derrop.minecraft.proxy.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionProvider {

    private static final Path PATH = Paths.get("permissions.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type permissionEntitiesType = TypeToken.getParameterized(Collection.class,  PermissionEntity.class).getType();

    private Map<UUID, PermissionEntity> permissionEntities = new HashMap<>();

    public PermissionProvider() {
        this.load();
    }

    public void load() {
        if (!Files.exists(PATH)) {
            return;
        }
        try (Reader reader = new InputStreamReader(Files.newInputStream(PATH), StandardCharsets.UTF_8)) {
            Collection<PermissionEntity> entities = this.gson.fromJson(reader, this.permissionEntitiesType);

            for (PermissionEntity entity : entities) {
                entity.setPermissions(entity.getPermissions().stream().map(String::toLowerCase).collect(Collectors.toList()));
                this.permissionEntities.put(entity.getUniqueId(), entity);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void save() {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(PATH), StandardCharsets.UTF_8)) {
            this.gson.toJson(this.permissionEntities.values(), writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void updatePermissionEntity(PermissionEntity entity) {
        entity.setPermissions(entity.getPermissions().stream().map(String::toLowerCase).collect(Collectors.toList()));
        this.permissionEntities.put(entity.getUniqueId(), entity);
        this.save();
    }

    public PermissionEntity getEntity(UUID uniqueId) {
        return this.permissionEntities.get(uniqueId);
    }

    public boolean hasPermission(UUID uniqueId, String permission) {
        PermissionEntity entity = this.getEntity(uniqueId);
        return entity != null && (entity.getPermissions().contains(permission) || entity.getPermissions().contains("*"));
    }

}
