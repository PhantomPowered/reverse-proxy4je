package com.github.derrop.proxy.api.ping;

import com.google.common.io.BaseEncoding;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class Favicon {

    private Favicon() {
        throw new UnsupportedOperationException();
    }

    public static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>() {
        @Override
        public void write(JsonWriter out, Favicon value) throws IOException {
            TypeAdapters.STRING.write(out, value == null ? null : value.encoded);
        }

        @Override
        public Favicon read(JsonReader in) throws IOException {
            String enc = TypeAdapters.STRING.read(in);
            return enc == null ? null : create(enc);
        }
    };

    private Favicon(String encoded) {
        this.encoded = encoded;
    }

    private final String encoded;

    @Nullable
    public static Favicon create(BufferedImage image) {
        if (image.getWidth() != 64 || image.getHeight() != 64) {
            throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", stream);

            byte[] imageBytes = stream.toByteArray();
            String encoded = "data:image/png;base64," + BaseEncoding.base64().encode(imageBytes);
            return encoded.length() > Short.MAX_VALUE ? null : new Favicon(encoded);
        } catch (IOException e) {
            return null;
        }
    }

    private static Favicon create(String encodedString) {
        return new Favicon(encodedString);
    }
}
