package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.ping.Favicon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.UnaryOperator;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static final Gson GSON = populate(new GsonBuilder(), GsonComponentSerializer.gson())
            .registerTypeAdapter(Favicon.class, Favicon.FAVICON_TYPE_ADAPTER)
            .create();

    public static String stringifyException(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : "");
    }

    public static UUID parseUUID(String uuid) {
        return UUID.fromString(Constants.UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
    }

    @SuppressWarnings("unchecked")
    private static GsonBuilder populate(GsonBuilder gsonBuilder, GsonComponentSerializer instance) {
        try {
            Field field = instance.getClass().getField("populator");
            field.setAccessible(true);
            return ((UnaryOperator<GsonBuilder>) field.get(instance)).apply(gsonBuilder);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }
}
