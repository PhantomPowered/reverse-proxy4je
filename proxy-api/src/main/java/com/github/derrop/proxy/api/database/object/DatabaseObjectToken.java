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

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.function.Function;

public abstract class DatabaseObjectToken<T> {

    public static final ThreadLocal<Gson> GSON = ThreadLocal.withInitial(() -> new Gson());

    @NotNull
    public static <T> DatabaseObjectToken<T> newToken(@NotNull Function<byte[], T> mapFunction,
                                                      @NotNull String key, @NotNull String table) {
        return new DatabaseObjectToken<T>() {

            @NotNull
            @Override
            public T deserialize(@NotNull byte[] bytes) {
                return mapFunction.apply(bytes);
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
    public static <T> DatabaseObjectToken<T> newToken(@NotNull String key, @NotNull String table, @NotNull Type type) {
        return newToken(bytes -> GSON.get().fromJson(new String(bytes), type), key, table);
    }

    @NotNull
    public abstract T deserialize(@NotNull byte[] bytes);

    @NotNull
    public abstract String getTable();

    @NotNull
    public abstract String getKey();
}
