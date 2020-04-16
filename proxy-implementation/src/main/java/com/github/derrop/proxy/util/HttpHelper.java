package com.github.derrop.proxy.util;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.util.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private HttpHelper() {
        throw new UnsupportedOperationException();
    }

    public static void getHTTPAsync(String url, Callback<String> callback) {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }

                    callback.done(outputStream.toString("UTF-8"), null);
                }

                connection.disconnect();

            } catch (IOException exception) {
                callback.done(null, exception);
            }
        });
    }

}
