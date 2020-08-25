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
package com.github.derrop.proxy.paste;

import com.github.derrop.proxy.api.paste.PasteServer;
import com.github.derrop.proxy.api.paste.PasteServerUploadResult;
import com.github.derrop.proxy.http.HttpUtil;
import com.github.derrop.proxy.util.LeftRightHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/* package */ class DefaultPasteServer implements PasteServer {

    private static final PasteServerUploadResult[] EMPTY_RESULT = new PasteServerUploadResult[0];

    protected DefaultPasteServer(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    private String apiUrl;
    private long limitPerDocument = 350_000;

    @Override
    public @NotNull String getUrl() {
        return this.apiUrl;
    }

    @Override
    public void setUrl(@NotNull String url) {
        this.apiUrl = url;
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getLimitPerDocument() {
        return this.limitPerDocument;
    }

    @Override
    public void setLimitPerDocument(@Range(from = 0, to = Long.MAX_VALUE) long limit) {
        this.limitPerDocument = limit;
    }

    @Override
    public @NotNull PasteServerUploadResult[] uploadDocumentSafely(@NotNull String content) {
        return this.uploadDocument(content);
    }

    @Override
    public @NotNull PasteServerUploadResult[] uploadDocument(@NotNull String content) {
        if (content.isEmpty()) {
            return EMPTY_RESULT;
        }

        String url = this.apiUrl; // hack
        if (!url.endsWith("/documents")) {
            url += (url.endsWith("/") ? "" : "/") + "documents";
        }

        Collection<String> documents = new ArrayList<>();
        do {
            int next = Math.min(content.length(), Math.toIntExact(this.limitPerDocument));
            documents.add(content.substring(0, next));
            content = content.substring(next);
        } while (!content.isEmpty());

        Collection<PasteServerUploadResult> output = new ArrayList<>(documents.size());
        for (String document : documents) {
            LeftRightHolder<String, IOException> result = HttpUtil.postSync(url, outputStream -> {
                try {
                    outputStream.write(document.getBytes(StandardCharsets.UTF_8));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            if (result.getRight() != null) {
                result.getRight().printStackTrace();
            } else {
                JsonObject jsonObject = JsonParser.parseString(result.getLeft()).getAsJsonObject();
                output.add(new DefaultPasteServerUploadResult(jsonObject, url, document));
            }
        }

        return output.toArray(new PasteServerUploadResult[0]);
    }
}
