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
