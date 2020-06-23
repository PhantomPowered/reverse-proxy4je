package com.github.derrop.proxy.util.player;

import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.player.PlayerId;
import com.github.derrop.proxy.api.util.player.PlayerIdRepository;
import com.github.derrop.proxy.api.util.player.PlayerRepositoryGetException;
import com.github.derrop.proxy.util.HttpHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultPlayerIdRepository extends DatabaseProvidedStorage<PlayerId> implements PlayerIdRepository {

    private static final long VALID_MILLIS = TimeUnit.DAYS.toMillis(3);
    private static final String NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String UUID_TO_NAME = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private final Cache<String, PlayerId> nameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();
    private final Cache<UUID, PlayerId> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();

    public DefaultPlayerIdRepository(ServiceRegistry registry) {
        super(registry, "player_id_repository_cache", PlayerId.class);
    }

    private void update(PlayerId playerId) {
        super.update(playerId.getName().toLowerCase(), playerId);
        super.update(playerId.getUniqueId().toString(), playerId);
        this.put(playerId);
    }

    private void insert(PlayerId playerId) {
        super.insert(playerId.getName().toLowerCase(), playerId);
        super.insert(playerId.getUniqueId().toString(), playerId);
        this.put(playerId);
    }

    private void put(PlayerId playerId) {
        this.nameCache.put(playerId.getName().toLowerCase(), playerId);
        this.uuidCache.put(playerId.getUniqueId(), playerId);
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
        return cached != null && cached.getTimestamp() + VALID_MILLIS >= System.currentTimeMillis();
    }

    @Override
    public PlayerId getPlayerId(@NotNull String name) {
        if (name.length() < 3) {
            return null;
        }

        String lowerName = name.toLowerCase();

        PlayerId cached = this.nameCache.getIfPresent(lowerName);
        if (this.isValid(cached)) {
            return cached;
        }
        cached = super.get(lowerName);
        if (this.isValid(cached)) {
            this.put(cached);
            return cached;
        }

        boolean isCached = cached != null;
        AtomicReference<PlayerId> reference = new AtomicReference<>();

        HttpHelper.getHTTPSync(String.format(NAME_TO_UUID, name), (result, error) -> this.set(reference, result, error, isCached));

        return reference.get();
    }

    @Override
    public PlayerId getPlayerId(@NotNull UUID uniqueId) {
        String uniqueIdString = uniqueId.toString();

        PlayerId cached = this.uuidCache.getIfPresent(uniqueId);
        if (this.isValid(cached)) {
            return cached;
        }
        cached = super.get(uniqueIdString);
        if (this.isValid(cached)) {
            this.put(cached);
            return cached;
        }

        boolean isCached = cached != null;
        AtomicReference<PlayerId> reference = new AtomicReference<>();

        HttpHelper.getHTTPSync(String.format(UUID_TO_NAME, uniqueIdString.replace("-", "")), (result, error) -> this.set(reference, result, error, isCached));

        return reference.get();
    }

    @Override
    public boolean isCached(@NotNull UUID uniqueId) {
        return this.uuidCache.asMap().containsKey(uniqueId) || this.isValid(super.get(uniqueId.toString()));
    }

    @Override
    public boolean isCached(@NotNull String name) {
        String lowerName = name.toLowerCase();
        return this.nameCache.asMap().containsKey(lowerName) || this.isValid(super.get(lowerName));
    }

}
