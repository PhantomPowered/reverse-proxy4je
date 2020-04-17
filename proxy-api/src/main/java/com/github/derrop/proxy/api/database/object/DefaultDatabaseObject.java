package com.github.derrop.proxy.api.database.object;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DefaultDatabaseObject implements DatabaseObject {

    private final String key;
    private final String table;
    private final Serializable serializable;

    public DefaultDatabaseObject(String key, String table, Serializable serializable) {
        this.key = key;
        this.table = table;
        this.serializable = serializable;
    }

    @Override
    public @NotNull String getKey() {
        return this.key;
    }

    @Override
    public @NotNull String getTable() {
        return this.table;
    }

    @Override
    public @NotNull byte[] serialize() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(this.serializable);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
