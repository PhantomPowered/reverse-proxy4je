package com.github.derrop.proxy.api.database.object;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;

public interface DatabaseObject extends Serializable {

    /**
     * @return The key of the database object
     */
    @NotNull
    String getKey();

    /**
     * @return The table in which the object should get added
     */
    @NotNull
    String getTable();

    /**
     * Encodes the current object for the database
     *
     * @return The encoded bytes from the current database object
     */
    @NotNull
    byte[] serialize() throws IOException;
}
