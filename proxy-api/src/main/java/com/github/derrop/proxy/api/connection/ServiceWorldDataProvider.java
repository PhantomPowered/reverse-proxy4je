package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.entity.player.GameMode;

public interface ServiceWorldDataProvider {

    long getWorldTime();

    long getTotalWorldTime();

    boolean isRaining();

    boolean isThundering();

    float getRainStrength();

    float getThunderStrength();

    GameMode getOwnGameMode();

}
