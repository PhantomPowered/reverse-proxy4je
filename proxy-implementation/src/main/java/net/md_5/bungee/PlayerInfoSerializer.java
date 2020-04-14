package net.md_5.bungee;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

// TODO: the hell?
@Deprecated
public class PlayerInfoSerializer implements JsonSerializer<ServerPing.PlayerInfo>, JsonDeserializer<ServerPing.PlayerInfo> {

    @Override
    public ServerPing.PlayerInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject js = json.getAsJsonObject();
        ServerPing.PlayerInfo info = new ServerPing.PlayerInfo(js.get("name").getAsString(), null);
        String id = js.get("id").getAsString();
        if (!id.contains("-")) {
            info.setUniqueId(Util.getUUID(id));
            if (info.getUniqueId() == null) {
                info.setUniqueId(UUID.randomUUID());
            }
        } else {
            info.setUniqueId(UUID.fromString(id));
        }
        return info;
    }

    @Override
    public JsonElement serialize(ServerPing.PlayerInfo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject out = new JsonObject();
        out.addProperty("name", src.getName());
        out.addProperty("id", src.getUniqueId().toString());
        return out;
    }
}
