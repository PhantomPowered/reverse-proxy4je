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
package com.github.derrop.proxy.permission;

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
