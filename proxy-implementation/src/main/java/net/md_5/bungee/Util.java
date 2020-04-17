package net.md_5.bungee;

import com.github.derrop.proxy.api.chat.component.*;
import com.google.common.primitives.UnsignedLongs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.chat.*;

import java.util.UUID;

/**
 * Series of utility classes to perform various operations.
 */
@Deprecated
public class Util {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
            .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
            .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
            .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();

    /**
     * Constructs a pretty one line version of a {@link Throwable}. Useful for
     * debugging.
     *
     * @param t the {@link Throwable} to format.
     * @return a string representing information about the {@link Throwable}
     */
    // TODO: move elsewhere
    public static String exception(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        return t.getClass().getSimpleName() + " : " + t.getMessage()
                + ((trace.length > 0) ? " @ " + t.getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber() : "");
    }

    /**
     * Converts a String to a UUID
     *
     * @param uuid The string to be converted
     * @return The result
     */
    // TODO: Looks unsafe, remove or remove
    public static UUID getUUID(String uuid) {
        return new UUID(UnsignedLongs.parseUnsignedLong(uuid.substring(0, 16), 16), UnsignedLongs.parseUnsignedLong(uuid.substring(16), 16));
    }
}
