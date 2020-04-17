package com.github.derrop.proxy.api.database;

import com.github.derrop.proxy.api.database.config.DatabaseConfig;
import com.github.derrop.proxy.api.database.object.DatabaseObject;
import com.github.derrop.proxy.api.database.object.DatabaseObjectToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DatabaseDriver {

    /**
     * Connects to the database
     *
     * @param config The config file for the database config
     * @return If the connection was successful
     */
    boolean connect(@NotNull DatabaseConfig config);

    /**
     * Creates a new table in the database
     *
     * @param table The name of the new table in the database
     */
    void createTable(@NotNull String table);

    /**
     * Deletes a table from the database
     *
     * @param table The name of the table which should get deleted
     */
    void deleteTable(@NotNull String table);

    /**
     * Inserts a database object into the database
     *
     * @param object The object which should get inserted
     */
    void insert(@NotNull DatabaseObject object);

    /**
     * Updates an object in the database
     *
     * @param object The object which should get updated
     */
    void update(@NotNull DatabaseObject object);

    /**
     * Gets an object from the database
     *
     * @param databaseObjectToken The token to deserialize the object from the database
     * @param <T>                 The type of the object which gets deserialize from the database
     * @return The deserialize object from the database
     */
    @Nullable
    default <T> T get(@NotNull DatabaseObjectToken<T> databaseObjectToken) {
        return getOrDefault(databaseObjectToken, null);
    }

    /**
     * Gets a value from the database or the default value if the key does not exists
     *
     * @param databaseObjectToken The token for the database object to load it
     * @param def                 The defined value which is used if the key does not exists
     * @param <T>                 The type of the object which gets deserialize from the database
     * @return The deserialize object from the database or the default one
     */
    @Nullable <T> T getOrDefault(@NotNull DatabaseObjectToken<T> databaseObjectToken, @Nullable T def);

    /**
     * Gets all objects from a specific table in the database
     *
     * @param table  The table in which the objects are located
     * @param mapper The mapper which creates the objects from the bytes of the database
     * @param <T>    The type of the object in the database after the map
     * @return A collection of all objects from a specific table in the database
     */
    @NotNull
    default <T> Collection<T> getAll(@NotNull String table, @NotNull Function<byte[], T> mapper) {
        Collection<T> out = new ArrayList<>();
        this.forEachInTable(table, mapper, out::add);
        return out;
    }

    /**
     * Accepts all values in the database to the consumer
     *
     * @param table   The table in which the objects are located
     * @param mapper  The mapper which creates the objects from the bytes of the database
     * @param handler Handles all objects which are read out of the database
     * @param <T>     The type of the object in the database after the map
     */
    <T> void forEachInTable(@NotNull String table, @NotNull Function<byte[], T> mapper, @NotNull Consumer<T> handler);

    /**
     * Deletes an object from the database
     *
     * @param table The table name from which the object should get deleted
     * @param key   The key of the database which should get deleted
     */
    void deleteFromTable(@NotNull String table, @NotNull String key);

    /**
     * Closes the current connection to the database
     */
    void close();
}
