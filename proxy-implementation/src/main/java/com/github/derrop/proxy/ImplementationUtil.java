package com.github.derrop.proxy;

import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.ping.Favicon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ImplementationUtil {

    private ImplementationUtil() {
        throw new UnsupportedOperationException();
    }

    public static final Gson GSON = GsonComponentSerializer.gson().populator().apply(new GsonBuilder().registerTypeAdapter(Favicon.class, Favicon.FAVICON_TYPE_ADAPTER)).create();

    public static String stringifyException(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : "");
    }

    public static UUID parseUUID(String uuid) {
        return UUID.fromString(APIUtil.UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
    }

    @NotNull
    public static String replaceLast(@NotNull String in, @NotNull String regex, @NotNull String replacement) {
        return in.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
