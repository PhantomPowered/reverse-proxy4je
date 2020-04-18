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
package com.github.derrop.proxy.api.database.object;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.Function;

public abstract class DatabaseObjectToken<T> {

    /**
     * Creates a new token to read the an object from the database.
     *
     * @param mapFunction The function which maps the stream to the required object
     * @param key         The key of the database object in the database itself
     * @param table       The table in which the database object is located
     * @param <T>         The type of the object which gets deserialize from the stream
     * @return The token to deserialize an object from the database
     */
    @NotNull
    public static <T> DatabaseObjectToken<T> newToken(@NotNull Function<ObjectInputStream, T> mapFunction,
                                                      @NotNull String key, @NotNull String table) {
        return new DatabaseObjectToken<T>() {
            @Override
            public @NotNull T deserialize(@NotNull ObjectInputStream stream) {
                return mapFunction.apply(stream);
            }

            @Override
            public @NotNull String getTable() {
                return table;
            }

            @Override
            public @NotNull String getKey() {
                return key;
            }
        };
    }

    @NotNull
    public static <T> DatabaseObjectToken<T> newToken(@NotNull String key, @NotNull String table) {
        return newToken(objectInputStream -> {
            try {
                return (T) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            return null;
        }, key, table);
    }

    /**
     * Deserializes an object from the database.
     *
     * @param stream The stream created from the database
     * @return The deserialize object from the given stream
     */
    @NotNull
    public abstract T deserialize(@NotNull ObjectInputStream stream) throws IOException;

    /**
     * @return The table in which the object is located
     */
    @NotNull
    public abstract String getTable();

    /**
     * @return The key of the database object in the database
     */
    @NotNull
    public abstract String getKey();
}
