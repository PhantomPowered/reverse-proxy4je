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
import com.github.derrop.proxy.api.paste.PasteServerProvider;
import com.github.derrop.proxy.api.paste.PasteServerUploadResult;
import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class DefaultPasteServerProvider implements PasteServerProvider {

    private final Map<String, PasteServer> cache = new MapMaker().concurrencyLevel(3).makeMap();

    @Override
    public @NotNull PasteServer getPasteServerForUrl(@NotNull String url) {
        if (this.cache.containsKey(url)) {
            return this.cache.get(url);
        }

        PasteServer pasteServer = new DefaultPasteServer(url);
        this.cache.put(url, pasteServer);
        return pasteServer;
    }

    @Override
    public @NotNull String[] getUrlsFromResults(@NotNull PasteServerUploadResult[] results) {
        return Arrays.stream(results)
                .filter(result -> result.getPasteUrl().isPresent())
                .map(result -> result.getPasteUrl().get())
                .toArray(String[]::new);
    }

    @Override
    public boolean isPasteServerCached(@NotNull String url) {
        return this.cache.containsKey(url);
    }

    @Override
    public void removeFromCache(@NotNull String url) {
        this.cache.remove(url);
    }
}
