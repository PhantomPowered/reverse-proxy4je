package com.github.derrop.proxy.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    public static void createDirectories(@NotNull Path path) {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                return;
            }

            try {
                Files.delete(path);
            } catch (final IOException ignored) {
            }
        }

        Path parent = path.getParent();
        if (parent != null) {
            createDirectories(parent);
        }

        try {
            Files.createDirectory(path);
        } catch (final IOException ignored) {
        }
    }

    @NotNull
    public static String replaceLast(@NotNull String in, @NotNull String regex, @NotNull String replacement) {
        return in.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
