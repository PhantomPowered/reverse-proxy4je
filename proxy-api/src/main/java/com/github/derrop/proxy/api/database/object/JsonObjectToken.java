package com.github.derrop.proxy.api.database.object;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;

public class JsonObjectToken<T> extends DatabaseObjectToken<T> {



    @NotNull
    @Override
    public T deserialize(@NotNull ObjectInputStream stream) throws IOException {
        return null;
    }

    @Override
    public @NotNull String getTable() {
        return null;
    }

    @Override
    public @NotNull String getKey() {
        return null;
    }
}
