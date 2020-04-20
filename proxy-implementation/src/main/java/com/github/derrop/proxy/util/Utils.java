package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.ping.Favicon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter())
            .create();

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static String stringifyException(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : "");
    }

    public static UUID parseUUID(String uuid) {
        return UUID.fromString(UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
    }

}
