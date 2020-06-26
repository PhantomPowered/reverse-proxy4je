package com.github.derrop.proxy.storage;

import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.player.id.PlayerId;
import com.github.derrop.proxy.api.player.id.PlayerIdRepository;
import com.github.derrop.proxy.api.player.id.PlayerRepositoryGetException;
import com.github.derrop.proxy.util.HttpHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultPlayerIdStorage extends DatabaseProvidedStorage<PlayerId> implements PlayerIdRepository {

    private static final long VALID_MILLIS = TimeUnit.DAYS.toMillis(3);
    private static final String NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String UUID_TO_NAME = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public DefaultPlayerIdStorage(ServiceRegistry registry) {
        super(registry, "player_id_repository_cache", PlayerId.class);
    }

    private void update(PlayerId playerId) {
        super.update(playerId.getName().toLowerCase(), playerId);
        super.update(playerId.getUniqueId().toString(), playerId);
    }

    private void insert(PlayerId playerId) {
        super.insert(playerId.getName().toLowerCase(), playerId);
        super.insert(playerId.getUniqueId().toString(), playerId);
    }

    private void set(AtomicReference<PlayerId> reference, String result, Throwable error, boolean cached) {
        if (error != null) {
            throw new PlayerRepositoryGetException(error);
        }

        JsonObject object = JsonParser.parseString(result).getAsJsonObject();
        if (!object.has("name") || !object.has("id")) {
            return;
        }

        UUID uniqueId = UUIDTypeAdapter.fromString(object.get("id").getAsString());
        reference.set(new PlayerId(uniqueId, object.get("name").getAsString()));

        if (cached) {
            this.update(reference.get());
        } else {
            this.insert(reference.get());
        }
    }

    private boolean isValid(PlayerId cached) {
        return cached != null && cached.getTimestamp() + VALID_MILLIS <= System.currentTimeMillis();
    }

    @Override
    public PlayerId getPlayerId(@NotNull String name) {
        PlayerId cached = super.get(name.toLowerCase());
        if (this.isValid(cached)) {
            return cached;
        }

        AtomicReference<PlayerId> reference = new AtomicReference<>();

        HttpHelper.getHTTPSync(String.format(NAME_TO_UUID, name), (result, error) -> this.set(reference, result, error, cached != null));

        return reference.get();
    }

    @Override
    public PlayerId getPlayerId(@NotNull UUID uniqueId) {
        String uniqueIdString = uniqueId.toString();
        PlayerId cached = super.get(uniqueIdString);
        if (this.isValid(cached)) {
            return cached;
        }

        AtomicReference<PlayerId> reference = new AtomicReference<>();

        HttpHelper.getHTTPSync(String.format(UUID_TO_NAME, uniqueIdString.replace("-", "")), (result, error) -> this.set(reference, result, error, cached != null));

        return reference.get();
    }

    @Override
    public boolean isCached(@NotNull UUID uniqueId) {
        return this.isValid(super.get(uniqueId.toString()));
    }

    @Override
    public boolean isCached(@NotNull String name) {
        return this.isValid(super.get(name.toLowerCase()));
    }

}
