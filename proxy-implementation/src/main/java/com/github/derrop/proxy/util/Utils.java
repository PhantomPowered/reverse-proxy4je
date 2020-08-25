package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.ping.Favicon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.UUID;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static final Gson GSON = GsonComponentSerializer.gson().populator().apply(new GsonBuilder().registerTypeAdapter(Favicon.class, Favicon.FAVICON_TYPE_ADAPTER)).create();

    public static String stringifyException(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : "");
    }

    public static UUID parseUUID(String uuid) {
        return UUID.fromString(Constants.UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
    }
}
