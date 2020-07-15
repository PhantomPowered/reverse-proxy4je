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
package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.util.PasteServerProvider;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DefaultPasteServerProvider implements PasteServerProvider {

    private String url = "https://just-paste.it";
    private long limitPerDocument = 350_000;

    @Override
    public @NotNull String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    @Override
    public long getLimitPerDocument() {
        return this.limitPerDocument;
    }

    @Override
    public void setLimitPerDocument(long limit) {
        Preconditions.checkArgument(limit > 0, "limit cannot be zero or smaller");
        this.limitPerDocument = limit;
    }

    @Override
    public String[] uploadDocumentCaught(@NotNull String content) {
        try {
            return this.uploadDocument(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String[] uploadDocument(@NotNull String content) throws IOException {
        if (content.isEmpty()) {
            return new String[0];
        }

        String url = this.url;

        if (!url.endsWith("/documents")) {
            url += (url.endsWith("/") ? "" : "/") + "documents";
        }

        Collection<String> documents = new ArrayList<>();
        do {
            int next = Math.min(content.length(), Math.toIntExact(this.limitPerDocument));
            documents.add(content.substring(0, next));
            content = content.substring(next);
        } while (!content.isEmpty());

        Collection<String> output = new ArrayList<>(documents.size());

        for (String document : documents) {
            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(document.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject()) {
                    output.add(element.getAsJsonObject().get("key").getAsString());
                }
            }
        }

        return output.toArray(new String[0]);
    }

    @Override
    public String[] mapAsURLs(@Nullable String[] keys) {
        return keys == null ? null : Arrays.stream(keys).map(key -> this.url + "/" + key).toArray(String[]::new);
    }
}
