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
package com.github.phantompowered.proxy.api.database;

import com.github.phantompowered.proxy.api.database.object.DatabaseObjectToken;
import com.github.phantompowered.proxy.api.database.object.DefaultDatabaseObject;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;

public class DatabaseProvidedStorage<T> {

    protected final ServiceRegistry registry;
    private final String table;
    private final Type type;

    public DatabaseProvidedStorage(ServiceRegistry registry, String table, Type type) {
        this.registry = registry;
        this.table = table;
        this.type = type;
        this.registry.getProviderUnchecked(DatabaseDriver.class).createTable(table);
    }

    protected void delete(String key) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).deleteFromTable(this.table, key);
    }

    protected @NotNull Collection<T> getAll() {
        return this.registry.getProviderUnchecked(DatabaseDriver.class).getAll(table, this.type);
    }

    protected T get(String key) {
        return this.registry.getProviderUnchecked(DatabaseDriver.class).get(DatabaseObjectToken.newToken(key, this.table, this.type));
    }

    protected void insert(String key, T value) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).insert(new DefaultDatabaseObject(key, this.table, value));
    }

    protected void update(String key, T value) {
        this.registry.getProviderUnchecked(DatabaseDriver.class).update(new DefaultDatabaseObject(key, this.table, value));
    }

    protected long size() {
        return this.registry.getProviderUnchecked(DatabaseDriver.class).count(this.table);
    }

    protected void insertOrUpdate(String key, T value) {
        if (this.get(key) != null) {
            this.update(key, value);
        } else {
            this.insert(key, value);
        }
    }

}
