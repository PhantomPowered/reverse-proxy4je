package com.github.derrop.proxy.config;

import com.google.gson.*;
import net.kyori.text.Component;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.lang.reflect.Type;

public class LegacyGsonComponentSerializer implements JsonSerializer<Component>, JsonDeserializer<Component> {
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LegacyComponentSerializer.legacy().deserialize(json.getAsString());
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(LegacyComponentSerializer.legacy().serialize(src));
    }
}
