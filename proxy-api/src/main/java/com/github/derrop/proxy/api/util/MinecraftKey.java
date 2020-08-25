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
package com.github.derrop.proxy.api.util;

import org.jetbrains.annotations.NotNull;

public final class MinecraftKey {

    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private static final char DEFAULT_DELIMITER = 58;

    public static @NotNull MinecraftKey of(@NotNull String name) {
        return of(name, DEFAULT_DELIMITER);
    }

    public static @NotNull MinecraftKey of(@NotNull String string, char character) {
        int index = string.indexOf(character);
        String namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        String value = index >= 0 ? string.substring(index + 1) : string;
        return of(namespace, value);
    }

    public static @NotNull MinecraftKey of(@NotNull String namespace, @NotNull String value) {
        return new MinecraftKey(namespace, value);
    }

    private final String key;
    private final String value;

    private MinecraftKey(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.toString(DEFAULT_DELIMITER);
    }

    public String toString(char delimiter) {
        return key + delimiter + value;
    }
}
