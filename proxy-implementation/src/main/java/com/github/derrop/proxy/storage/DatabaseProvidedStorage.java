package com.github.derrop.proxy.storage;

import com.github.derrop.proxy.api.database.DatabaseDriver;
import com.github.derrop.proxy.api.database.object.DatabaseObjectToken;
import com.github.derrop.proxy.api.database.object.DefaultDatabaseObject;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;

public class DatabaseProvidedStorage<T extends Serializable> {

    private final ServiceRegistry registry;
    private final String table;

    public DatabaseProvidedStorage(ServiceRegistry registry, String table) {
        this.registry = registry;
        this.table = table;
        this.registry.getProviderUnchecked(DatabaseDriver.class).createTable(table);
    }

    protected void delete(String key) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).deleteFromTable(this.table, key);
    }

    protected @NotNull Collection<T> getAll() {
        return this.registry.getProviderUnchecked(DatabaseDriver.class).getAll(table, bytes -> {
            try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (T) inputStream.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            return null;
        });
    }

    protected T get(String key) {
        return this.registry.getProviderUnchecked(DatabaseDriver.class).get(DatabaseObjectToken.newToken(key, this.table));
    }

    protected void insert(String key, T value) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).insert(new DefaultDatabaseObject(key, this.table, value));
    }

    protected void update(String key, T value) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).update(new DefaultDatabaseObject(key, this.table, value));
    }

    protected void insertOrUpdate(String key, T value) {
        if (this.get(key) != null) {
            this.update(key, value);
        } else {
            this.insert(key, value);
        }
    }

}
