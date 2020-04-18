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
package com.github.derrop.proxy.plugins.gomme.player;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.player.clan.ClanInfo;

import java.util.UUID;

// TODO database
public class PlayerDataProvider {

    public PlayerData getData(UUID uniqueId) {
        return null;
    }

    public void updateStatistics(PlayerData data) {

    }

    public long countPlayerData() {
        return -1;
    }

    public long countPlayerData(GommeGameMode gameMode) {
        return -1;
    }

    public PlayerData getBestPlayer(GommeGameMode gameMode) {
        return null;
    }

    public PlayerData getWorstPlayer(GommeGameMode gameMode) {
        return null;
    }

    public ClanInfo getClan(String name) {
        return null;
    }

    public ClanInfo getClanByShortcut(String shortcut) {
        return null;
    }

    public void updateClan(ClanInfo info) {

    }

}
