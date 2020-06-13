package com.github.derrop.proxy.api.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class PasteServerUtils {

    private PasteServerUtils() {
        throw new UnsupportedOperationException();
    }

    public static String uploadCatched(String url, String content) {
        try {
            return upload(url, content);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static String upload(String url, String content) throws IOException {
        if (!url.endsWith("/documents")) {
            url += (url.endsWith("/") ? "" : "/") + "documents";
        }

        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(content.getBytes());
        }

        try (InputStream inputStream = connection.getInputStream();
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                return element.getAsJsonObject().get("key").getAsString();
            }
        }

        return null;
    }

}
