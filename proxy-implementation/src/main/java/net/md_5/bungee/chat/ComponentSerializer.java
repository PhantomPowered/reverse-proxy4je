package net.md_5.bungee.chat;

import com.github.derrop.proxy.api.chat.component.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Set;

public class ComponentSerializer implements JsonDeserializer<BaseComponent> {

    private static final Gson gson = new GsonBuilder().
            registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).
            registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).
            registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).
            registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer()).
            registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer()).
            registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer()).
            create();

    public static final ThreadLocal<Set<BaseComponent>> serializedComponents = new ThreadLocal<>();

    public static BaseComponent[] parse(String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if (jsonElement.isJsonArray()) {
                return gson.fromJson(jsonElement, BaseComponent[].class);
            } else {
                return new BaseComponent[]{gson.fromJson(jsonElement, BaseComponent.class)};
            }
        } catch (final Throwable ex) {
            return new BaseComponent[0];
        }
    }

    public static String toString(BaseComponent component) {
        return gson.toJson(component);
    }

    public static String toString(BaseComponent... components) {
        if (components.length == 1) {
            return gson.toJson(components[0]);
        } else {
            return gson.toJson(new TextComponent(components));
        }
    }

    @Override
    public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return new TextComponent(json.getAsString());
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has("translate")) {
            return context.deserialize(json, TranslatableComponent.class);
        }
        if (object.has("keybind")) {
            return context.deserialize(json, KeybindComponent.class);
        }
        if (object.has("score")) {
            return context.deserialize(json, ScoreComponent.class);
        }
        if (object.has("selector")) {
            return context.deserialize(json, SelectorComponent.class);
        }
        return context.deserialize(json, TextComponent.class);
    }
}
