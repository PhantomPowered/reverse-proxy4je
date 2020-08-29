package com.github.phantompowered.proxy;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.ping.Favicon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ImplementationUtil {

    public static final String SERVER_BRAND_PREFIX = "PhantomProxy <-> ";

    public static final Gson GSON = GsonComponentSerializer.gson().populator().apply(new GsonBuilder().registerTypeAdapter(Favicon.class, Favicon.FAVICON_TYPE_ADAPTER)).create();
    public static final Gson GAME_PROFILE_GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .create();

    private ImplementationUtil() {
        throw new UnsupportedOperationException();
    }

    public static String stringifyException(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        String firstLine = trace.length > 0 ? " @ " + trace[0].getClassName() + ":" + trace[0].getLineNumber() : "";
        return t.getClass().getSimpleName() + ": " + t.getMessage() + firstLine;
    }

    public static UUID parseUUID(String uuid) {
        return UUID.fromString(APIUtil.UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5"));
    }

    @NotNull
    public static String replaceLast(@NotNull String in, @NotNull String regex, @NotNull String replacement) {
        return in.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
