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

import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.math.MathHelper;
import com.github.derrop.proxy.plugins.gommecw.running.ClanWarTeam;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;

import java.util.UUID;

public class DefaultHighlightListener implements GommeCWHighlightListener {

    private static final double MAX_DAMAGE_DISTANCE = MathHelper.square(APIUtil.SURVIVAL_PLACE_DISTANCE + 5);

    @Override
    public void handleTick(RunningClanWar clanWar) {
        if (clanWar.getFrame() == null) {
            return;
        }

        if ("damage".equals(clanWar.getFrame().getLastSwitchReason())) {
            EntityPlayer damager = (EntityPlayer) clanWar.getProperties().get("damager");
            EntityPlayer damaged = (EntityPlayer) clanWar.getProperties().get("damaged");

            if (damaged == null || damager == null) {
                return;
            }

            if (clanWar.getFrame().isLastSwitchLongerThan(5000)) {
                return;
            }

            if (damaged.getLocation().distanceSquared(damager.getLocation()) >= MAX_DAMAGE_DISTANCE) {
                clanWar.getFrame().togglePlayerCamera(null, null, "leave_damage_range");
            }

            return;
        }

        clanWar.getProperties().remove("damager");
        clanWar.getProperties().remove("damaged");
    }

    @Override
    public void handleDamage(RunningClanWar clanWar, ClanWarTeam damagerTeam, ClanWarTeam damagedTeam, EntityPlayer damaged, EntityPlayer damager) {
        // TODO not being called correctly
        damagerTeam.getMembers().stream().filter(member -> member.getUniqueId().equals(damager.getUniqueId())).findFirst().ifPresent(member -> {
            System.out.println("Damage to " + damaged.getPlayerInfo().getUsername() + " from " + damager.getPlayerInfo().getUsername());
            if (clanWar.getFrame().togglePlayerCamera(damagerTeam, member, "damage")) {
                clanWar.getProperties().put("damaged", damaged);
                clanWar.getProperties().put("damager", damager);
            }
        });
    }

    @Override
    public void handleDeath(RunningClanWar clanWar, PlayerInfo playerInfo) {
        UUID current = clanWar.getFrame().getCurrentDisplayedPlayer();
        if (current != null && playerInfo.getUniqueId().equals(current)) {
            clanWar.getFrame().togglePlayerCamera(null, null, "death");
        }
    }

    @Override
    public void handleNearBed(RunningClanWar clanWar, Location bedLocation, PlayerInfo playerInfo, ClanWarTeam team) {
        // TODO check if the bed exists
        System.out.println("near");
        clanWar.getTeams().stream().filter(filter -> bedLocation.equals(filter.getBedLocation())).findFirst()
                .ifPresent(bedTeam -> {
                    if (bedTeam.getColor() == team.getColor()) {
                        return;
                    }

                    team.getMembers().stream().filter(member -> member.getUniqueId().equals(playerInfo.getUniqueId())).findFirst().ifPresent(member -> {
                        clanWar.getFrame().togglePlayerCamera(team, member, "near_bed");
                        System.out.println(playerInfo.getUsername() + " is near the bed of " + bedTeam.getColor());
                    });
                });
    }

    @Override
    public void handleAwayFromBed(RunningClanWar clanWar, Location bedLocation, PlayerInfo playerInfo, ClanWarTeam team) {
        clanWar.getTeams().stream().filter(filter -> bedLocation.equals(filter.getBedLocation())).findFirst()
                .ifPresent(bedTeam -> {
                    if (bedTeam.getColor() == team.getColor()) {
                        return;
                    }

                    if (playerInfo.getUniqueId().equals(clanWar.getFrame().getCurrentDisplayedPlayer())
                            && "near_bed".equals(clanWar.getFrame().getLastSwitchReason())) {
                        clanWar.getFrame().togglePlayerCamera(null, null, "away_bed");
                    }
                    System.out.println(playerInfo.getUsername() + " is away from the bed of " + bedTeam.getColor());
                });
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
