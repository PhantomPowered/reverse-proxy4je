package com.github.derrop.proxy.plugins.gomme.match;

import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MatchEventSerializer implements JsonSerializer<MatchEvent>, JsonDeserializer<MatchEvent> {
    private static final String MATCH_EVENT_NAME = MatchEvent.class.getName();
    private static final String PREFIX = MATCH_EVENT_NAME.substring(0, MATCH_EVENT_NAME.length() - MatchEvent.class.getSimpleName().length());

    @Override
    public MatchEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return null;
        }
        JsonObject object = json.getAsJsonObject();
        String className = object.get("class").getAsString();
        if (className == null) {
            return null;
        }
        try {
            Class<?> eventClass = Class.forName(PREFIX + className);
            return context.deserialize(json, eventClass);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(MatchEvent src, Type typeOfSrc, JsonSerializationContext context) {
        String className = src.getClass().getName().substring(PREFIX.length());
        JsonObject object = context.serialize(src).getAsJsonObject();
        object.addProperty("class", className);
        return object;
    }
}
