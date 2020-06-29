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
package com.github.derrop.proxy.plugins.gommecw.highlights;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.plugins.gommecw.running.ClanWarTeam;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;

public class DefaultHighlightListener implements GommeCWHighlightListener {
    @Override
    public void handleDamage(RunningClanWar clanWar, ClanWarTeam damagerTeam, ClanWarTeam damagedTeam, EntityPlayer damaged, EntityPlayer damager) {
        // TODO not being called correctly
        System.out.println("Damage to " + damaged.getPlayerInfo().getUsername() + " from " + damager.getPlayerInfo().getUsername());
        // TODO switch frame to the damager, switch back when the distance between damaged and damager is greater than Constants.SURVIVAL_PLACE_DISTANCE + 5
    }

    @Override
    public void handleDeath(RunningClanWar clanWar, PlayerInfo playerInfo) {
        // TODO close the frame of the player
    }

    @Override
    public void handleNearBed(RunningClanWar clanWar, PlayerInfo playerInfo, ClanWarTeam team) {
        // TODO switch frame to the player, but ONLY IF the bed is not owned by the player's team
    }

    @Override
    public void handleAwayFromBed(RunningClanWar clanWar, PlayerInfo playerInfo, ClanWarTeam team) {
        // TODO switch frame back if the player is dead or for more than 5 seconds away from the bed
    }

    @Override
    public void handleBowAim(RunningClanWar clanWar, EntityPlayer player) {
        // TODO switch frame to the player until he stops with shooting
    }

    @Override
    public void handleStopBowAim(RunningClanWar clanWar, EntityPlayer player) {
        // TODO switch back when there was no bow aim for 5 seconds
    }
}
