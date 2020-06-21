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
package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.player.PlayerDataProvider;
import com.github.derrop.proxy.plugins.gomme.player.clan.ClanInfoProvider;
import com.github.derrop.proxy.plugins.gomme.secret.GommeSpectatorDetector;

// TODO SheepTimer: DisplayName or if that doesn't work properly ArmorStand above the sheep
public class GommeStatsCore {

    private final MatchManager matchManager;
    private final PlayerDataProvider playerDataProvider;
    private final ClanInfoProvider clanInfoProvider;

    private final GommeSpectatorDetector spectatorDetector = new GommeSpectatorDetector(this);

    private final ServiceRegistry registry;

    public GommeStatsCore(ServiceRegistry registry) {
        this.registry = registry;
        this.matchManager = new MatchManager(this);
        this.playerDataProvider = new PlayerDataProvider(this.registry);
        this.clanInfoProvider = new ClanInfoProvider(this.registry);
    }

    public MatchManager getMatchManager() {
        return this.matchManager;
    }

    public PlayerDataProvider getPlayerDataProvider() {
        return this.playerDataProvider;
    }

    public ClanInfoProvider getClanInfoProvider() {
        return this.clanInfoProvider;
    }

    public ServiceRegistry getRegistry() {
        return this.registry;
    }

    public GommeSpectatorDetector getSpectatorDetector() {
        return this.spectatorDetector;
    }
}
