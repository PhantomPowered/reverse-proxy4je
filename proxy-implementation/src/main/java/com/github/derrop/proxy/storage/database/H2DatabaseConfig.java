package com.github.derrop.proxy.storage.database;

import com.github.derrop.proxy.api.database.config.DatabaseConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class H2DatabaseConfig implements DatabaseConfig {

    private static final String END_POINT = new File("database").getAbsolutePath();

    @Override
    public @NotNull String getConnectionEndpoint() {
        return END_POINT;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public @Nullable String getUserName() {
        return null;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public @NotNull String getDatabaseName() {
        return "h2";
    }
}
