/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.connection.player.GameMode;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.entity.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface ServiceWorldDataProvider {

    long getWorldTime();

    long getTotalWorldTime();

    boolean isRaining();

    boolean isThundering();

    float getRainStrength();

    float getThunderStrength();

    @NotNull
    GameMode getOwnGameMode();

    @NotNull
    PlayerInfo[] getOnlinePlayers();

    PlayerInfo getOnlinePlayer(@NotNull UUID uniqueId);

    @NotNull
    Collection<EntityPlayer> getPlayersInWorld();

    EntityPlayer getPlayerInWorld(@NotNull UUID uniqueId);

    @NotNull
    Collection<? extends Entity> getEntitiesInWorld();

    Entity getEntityInWorld(int entityId);

}
