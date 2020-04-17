package com.github.derrop.proxy.api.database.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DatabaseConfig {

    @NotNull String getConnectionEndpoint();

    int getPort();

    @Nullable String getUserName();

    @Nullable String getPassword();

    @NotNull String getDatabaseName();
}
