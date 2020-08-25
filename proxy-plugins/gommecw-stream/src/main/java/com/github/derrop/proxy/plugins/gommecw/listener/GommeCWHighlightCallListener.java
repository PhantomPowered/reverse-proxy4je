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
package com.github.derrop.proxy.plugins.gommecw.listener;

import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.tick.TickHandler;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.EntityStatusType;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.entity.status.EntityStatusEvent;
import com.github.derrop.proxy.plugins.gomme.events.GommeMatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerDiedEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerKilledEvent;
import com.github.derrop.proxy.plugins.gommecw.GommeCWPlugin;
import com.github.derrop.proxy.plugins.gommecw.highlights.GommeCWHighlightListener;
import com.github.derrop.proxy.plugins.gommecw.running.ClanWarTeam;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GommeCWHighlightCallListener implements TickHandler {

    private static final Object EMPTY_OBJECT = new Object();
    private static final String BOW_PROPERTY = "GommeCW-BowShooting";

    private final GommeCWPlugin plugin;
    private final GommeCWHighlightListener listener;

    public GommeCWHighlightCallListener(@NotNull GommeCWPlugin plugin, @NotNull GommeCWHighlightListener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    @Listener
    public void handleEntityDamage(EntityStatusEvent event) {
        ServiceConnection connection = event.getConnection();
        Entity entity = event.getEntity();
        if (event.getStatusType() != EntityStatusType.DAMAGE || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;

        this.plugin.getCwManager().getClanWar(connection).ifPresent(clanWar -> {

            ClanWarTeam damagedTeam = clanWar.getTeam(player.getUniqueId());
            if (damagedTeam == null) {
                return;
            }

            Collection<EntityPlayer> players = connection.getWorldDataProvider().getNearbyPlayers(APIUtil.HIT_DISTANCE);
            if (players.isEmpty()) {
                return;
            }

            for (EntityPlayer damager : players) {
                ClanWarTeam damagerTeam = clanWar.getTeam(damager.getUniqueId());
                if (damagerTeam == null || damagedTeam.getColor() == damagerTeam.getColor()) {
                    continue;
                }

                this.handleDamage(clanWar, damagerTeam, damagedTeam, player, damager);
                break;
            }
        });
    }

    @Listener
    public void handleDeath(GommeMatchEvent event) {
        if (!(event.getMatchEvent() instanceof PlayerDiedEvent)) {
            return;
        }

        this.handleDied(event.getMatchInfo(), ((PlayerDiedEvent) event.getMatchEvent()).getPlayer());
    }

    @Listener
    public void handleKill(GommeMatchEvent event) {
        if (!(event.getMatchEvent() instanceof PlayerKilledEvent)) {
            return;
        }

        this.handleDied(event.getMatchInfo(), ((PlayerKilledEvent) event.getMatchEvent()).getPlayer());
    }

    private void handleDamage(RunningClanWar clanWar, ClanWarTeam damagerTeam, ClanWarTeam damagedTeam, EntityPlayer damaged, EntityPlayer damager) {
        this.listener.handleDamage(clanWar, damagerTeam, damagedTeam, damaged, damager);
    }

    private void handleDied(MatchInfo matchInfo, String player) {
        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(matchInfo.getMatchId());
        if (clanWar == null) {
            return;
        }

        matchInfo.getPlayers().stream()
                .filter(playerInfo -> playerInfo.getUsername().equals(player)).findFirst()
                .ifPresent(playerInfo -> this.listener.handleDeath(clanWar, playerInfo));
    }

    @Listener
    public void handleNearbyBed(GommeMatchEvent event) {
        if (!(event.getMatchEvent() instanceof BedJoinEvent)) {
            return;
        }
        BedJoinEvent bedEvent = (BedJoinEvent) event.getMatchEvent();

        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(event.getMatchInfo().getMatchId());
        if (clanWar == null) {
            return;
        }
        ClanWarTeam team = clanWar.getTeam(bedEvent.getPlayer().getUniqueId());
        if (team == null) {
            return;
        }

        this.listener.handleNearBed(clanWar, bedEvent.getBed(), bedEvent.getPlayer(), team);
    }

    @Listener
    public void handleAwayFromBed(GommeMatchEvent event) {
        if (!(event.getMatchEvent() instanceof BedLeaveEvent)) {
            return;
        }
        BedLeaveEvent bedEvent = (BedLeaveEvent) event.getMatchEvent();

        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(event.getMatchInfo().getMatchId());
        if (clanWar == null) {
            return;
        }
        ClanWarTeam team = clanWar.getTeam(bedEvent.getPlayer().getUniqueId());
        if (team == null) {
            return;
        }

        this.listener.handleAwayFromBed(clanWar, bedEvent.getBed(), bedEvent.getPlayer(), team);
    }

    @Override
    public void handleTick() {
        for (RunningClanWar clanWar : this.plugin.getCwManager().getClanWars()) {
            for (ServiceConnection connection : clanWar.getOurSpectators()) {
                for (EntityPlayer player : connection.getWorldDataProvider().getPlayersInWorld()) {
                    String property = BOW_PROPERTY + "-" + player.getUniqueId().toString();
                    boolean hasProperty = clanWar.getMatchInfo().getProperty(property) != null;

                    if (player.isShootingWithBow()) {
                        if (hasProperty) {
                            continue;
                        }
                        clanWar.getMatchInfo().setProperty(property, EMPTY_OBJECT);

                        this.listener.handleBowAim(clanWar, player);
                    } else if (hasProperty) {
                        clanWar.getMatchInfo().getProperties().remove(property);

                        this.listener.handleStopBowAim(clanWar, player);
                    }
                }
            }
            this.listener.handleTick(clanWar);
        }
    }
}
