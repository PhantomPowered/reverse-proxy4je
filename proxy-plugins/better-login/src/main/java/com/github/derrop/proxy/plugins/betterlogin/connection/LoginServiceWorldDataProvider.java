package com.github.derrop.proxy.plugins.betterlogin.connection;

import com.github.derrop.proxy.api.connection.ServiceWorldDataProvider;
import com.github.derrop.proxy.api.connection.player.GameMode;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.entity.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class LoginServiceWorldDataProvider implements ServiceWorldDataProvider {
    @Override
    public long getWorldTime() {
        return 0;
    }

    @Override
    public long getTotalWorldTime() {
        return 0;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public float getRainStrength() {
        return 0;
    }

    @Override
    public float getThunderStrength() {
        return 0;
    }

    @Override
    public @NotNull GameMode getOwnGameMode() {
        return GameMode.CREATIVE;
    }

    @Override
    public PlayerInfo[] getOnlinePlayers() {
        return new PlayerInfo[0];
    }

    @Override
    public PlayerInfo getOnlinePlayer(@NotNull UUID uniqueId) {
        return null;
    }

    @Override
    public PlayerInfo getOnlinePlayer(@NotNull String name) {
        return null;
    }

    @Override
    public @NotNull Collection<EntityPlayer> getPlayersInWorld() {
        return Collections.emptyList();
    }

    @Override
    public EntityPlayer getPlayerInWorld(@NotNull UUID uniqueId) {
        return null;
    }

    @Override
    public @NotNull Collection<? extends Entity> getEntitiesInWorld() {
        return Collections.emptyList();
    }

    @Override
    public Entity getEntityInWorld(int entityId) {
        return null;
    }
}
