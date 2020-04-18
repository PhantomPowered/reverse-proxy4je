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
package com.github.derrop.proxy.api.chat;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class TranslationRegistry {

    public static final TranslationRegistry INSTANCE = new TranslationRegistry();
    //
    private final List<TranslationProvider> providers = new LinkedList<>();

    static {
        try {
            INSTANCE.addProvider(new JsonProvider("/assets/minecraft/lang/en_us.json"));
        } catch (Exception ex) {
        }

        try {
            INSTANCE.addProvider(new JsonProvider("/mojang-translations/en_us.json"));
        } catch (Exception ex) {
        }

        try {
            INSTANCE.addProvider(new ResourceBundleProvider("mojang-translations/en_US"));
        } catch (Exception ex) {
        }
    }

    private TranslationRegistry() {
    }

    private void addProvider(TranslationProvider provider) {
        providers.add(provider);
    }

    public String translate(String s) {
        for (TranslationProvider provider : providers) {
            String translation = provider.translate(s);

            if (translation != null) {
                return translation;
            }
        }

        return s;
    }

    public List<TranslationProvider> getProviders() {
        return this.providers;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TranslationRegistry)) return false;
        final TranslationRegistry other = (TranslationRegistry) o;
        final Object this$providers = this.getProviders();
        final Object other$providers = other.getProviders();
        if (this$providers == null ? other$providers != null : !this$providers.equals(other$providers))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $providers = this.getProviders();
        result = result * PRIME + ($providers == null ? 43 : $providers.hashCode());
        return result;
    }

    public String toString() {
        return "TranslationRegistry(providers=" + this.getProviders() + ")";
    }

    private interface TranslationProvider {

        String translate(String s);
    }

    private static class ResourceBundleProvider implements TranslationProvider {

        private final ResourceBundle bundle;

        public ResourceBundleProvider(String bundlePath) {
            this.bundle = ResourceBundle.getBundle(bundlePath);
        }

        public ResourceBundleProvider(ResourceBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public String translate(String s) {
            return (bundle.containsKey(s)) ? bundle.getString(s) : null;
        }

        public ResourceBundle getBundle() {
            return this.bundle;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof ResourceBundleProvider))
                return false;
            final ResourceBundleProvider other = (ResourceBundleProvider) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$bundle = this.getBundle();
            final Object other$bundle = other.getBundle();
            if (this$bundle == null ? other$bundle != null : !this$bundle.equals(other$bundle))
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof ResourceBundleProvider;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $bundle = this.getBundle();
            result = result * PRIME + ($bundle == null ? 43 : $bundle.hashCode());
            return result;
        }

        public String toString() {
            return "TranslationRegistry.ResourceBundleProvider(bundle=" + this.getBundle() + ")";
        }
    }

    private static class JsonProvider implements TranslationProvider {

        private final Map<String, String> translations = new HashMap<>();

        public JsonProvider(String resourcePath) throws IOException {
            try (InputStreamReader rd = new InputStreamReader(JsonProvider.class.getResourceAsStream(resourcePath), Charsets.UTF_8)) {
                JsonObject obj = new Gson().fromJson(rd, JsonObject.class);
                for (Map.Entry<String, JsonElement> entries : obj.entrySet()) {
                    translations.put(entries.getKey(), entries.getValue().getAsString());
                }
            }
        }

        public JsonProvider() {
        }

        @Override
        public String translate(String s) {
            return translations.get(s);
        }

        public Map<String, String> getTranslations() {
            return this.translations;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof JsonProvider))
                return false;
            final JsonProvider other = (JsonProvider) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$translations = this.getTranslations();
            final Object other$translations = other.getTranslations();
            if (this$translations == null ? other$translations != null : !this$translations.equals(other$translations))
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof JsonProvider;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $translations = this.getTranslations();
            result = result * PRIME + ($translations == null ? 43 : $translations.hashCode());
            return result;
        }

        public String toString() {
            return "TranslationRegistry.JsonProvider()";
        }
    }
}
