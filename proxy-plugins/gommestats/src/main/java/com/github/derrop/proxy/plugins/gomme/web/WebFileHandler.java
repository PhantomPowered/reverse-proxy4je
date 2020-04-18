package com.github.derrop.proxy.plugins.gomme.web;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class WebFileHandler implements Handler {

    private final String pathPrefix;

    public WebFileHandler(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public abstract String finalLoadFile(String path, Map<String, String> queryParameters);

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Map<String, String> queryParameters = new HashMap<>();
        for (String key : ctx.queryParamMap().keySet()) {
            queryParameters.put(key, ctx.queryParam(key));
        }

        String path = ctx.path().substring(this.pathPrefix.length());

        String result = this.finalLoadFile(path, queryParameters);

        if (result == null) {
            ctx.status(404);
            return;
        }

        ctx
                .result(result)
                .header("Content-Type", "text/html");
    }

    protected String loadFile(String path) {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                return outputStream.toString("UTF-8");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
