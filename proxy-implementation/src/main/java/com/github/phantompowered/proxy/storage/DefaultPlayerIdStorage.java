package com.github.phantompowered.proxy.storage;

import com.github.phantompowered.proxy.api.database.DatabaseProvidedStorage;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.player.id.PlayerIdStorage;
import com.github.phantompowered.proxy.api.player.id.PlayerRepositoryGetException;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.http.HttpUtil;
import com.github.phantompowered.proxy.util.LeftRightHolder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultPlayerIdStorage extends DatabaseProvidedStorage<PlayerId> implements PlayerIdStorage {

    private static final long VALID_MILLIS = TimeUnit.DAYS.toMillis(3);
    private static final String NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String UUID_TO_NAME = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private final Cache<String, PlayerId> nameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .concurrencyLevel(3)
            .build();
    private final Cache<UUID, PlayerId> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .concurrencyLevel(3)
            .build();

    public DefaultPlayerIdStorage(ServiceRegistry registry) {
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

    private @Nullable PlayerId set(String result, boolean update) {
        JsonObject object = JsonParser.parseString(result).getAsJsonObject();
        if (!object.has("name") || !object.has("id")) {
            return null;
        }

        UUID uniqueId = UUIDTypeAdapter.fromString(object.get("id").getAsString());
        PlayerId playerId = new PlayerId(uniqueId, object.get("name").getAsString());

        if (update) {
            this.update(playerId);
        } else {
            this.insert(playerId);
        }

        return playerId;
    }

    private boolean isValid(PlayerId cached) {
        return cached != null && cached.getTimestamp() + VALID_MILLIS >= System.currentTimeMillis();
    }

    @Override
    public PlayerId getPlayerId(@NotNull String name) {
        LeftRightHolder<PlayerId, Boolean> cached = this.getPlayerId0(name, null);
        if (cached.getRight() && cached.getLeft() != null) {
            return cached.getLeft();
        }

        LeftRightHolder<String, IOException> result = HttpUtil.getSync(String.format(NAME_TO_UUID, name));
        if (result.getRight() == null) {
            return this.set(result.getLeft(), cached.getRight());
        }

        throw new PlayerRepositoryGetException(result.getRight());
    }

    @Override
    public PlayerId getPlayerId(@NotNull UUID uniqueId) {
        LeftRightHolder<PlayerId, Boolean> cached = this.getPlayerId0(null, uniqueId);
        if (cached.getRight() && cached.getLeft() != null) {
            return cached.getLeft();
        }

        LeftRightHolder<String, IOException> result = HttpUtil.getSync(String.format(UUID_TO_NAME, uniqueId.toString().replace("-", "")));
        if (result.getRight() == null) {
            return this.set(result.getLeft(), cached.getRight());
        }

        throw new PlayerRepositoryGetException(result.getRight());
    }

    @Override
    public PlayerId getPlayerId(@Nullable String name, @Nullable UUID uniqueId) {
        LeftRightHolder<PlayerId, Boolean> result = this.getPlayerId0(name, uniqueId);
        if (result.getRight()) {
            return result.getLeft();
        }

        return null;
    }

    public @NotNull LeftRightHolder<PlayerId, Boolean> getPlayerId0(@Nullable String name, @Nullable UUID uniqueId) {
        if (uniqueId != null) {
            String uniqueIdString = uniqueId.toString();
            PlayerId cached = this.uuidCache.getIfPresent(uniqueId);
            if (this.isValid(cached)) {
                return LeftRightHolder.of(cached, true);
            }

            cached = super.get(uniqueIdString);
            if (this.isValid(cached)) {
                this.put(cached);
                return LeftRightHolder.of(cached, true);
            } else {
                return LeftRightHolder.of(cached, false);
            }
        }

        if (name != null) {
            if (name.length() < 3) {
                return LeftRightHolder.of(null, false);
            }

            String lowerName = name.toLowerCase();
            PlayerId cached = this.nameCache.getIfPresent(lowerName);
            if (this.isValid(cached)) {
                return LeftRightHolder.of(cached, true);
            }

            cached = super.get(lowerName);
            if (this.isValid(cached)) {
                this.put(cached);
                return LeftRightHolder.of(cached, true);
            } else {
                return LeftRightHolder.of(cached, false);
            }
        }

        return LeftRightHolder.of(null, false);
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
