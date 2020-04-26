package com.github.derrop.proxy.plugins.betterlogin.connection;

import com.github.derrop.proxy.api.connection.ServiceWorldDataProvider;
import com.github.derrop.proxy.api.entity.player.GameMode;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;

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
    public GameMode getOwnGameMode() {
        return GameMode.CREATIVE;
    }

    @Override
    public PlayerInfo[] getOnlinePlayers() {
        return new PlayerInfo[0];
    }

    @Override
    public PlayerInfo getOnlinePlayer(UUID uniqueId) {
        return null;
    }
}
