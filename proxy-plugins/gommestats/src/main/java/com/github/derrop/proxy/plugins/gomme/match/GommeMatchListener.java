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
package com.github.derrop.proxy.plugins.gomme.match;

import com.github.derrop.proxy.api.block.BlockState;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.block.half.VerticalHalf;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerMoveEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerServiceSelectedEvent;
import com.github.derrop.proxy.api.events.connection.service.ServiceDisconnectEvent;
import com.github.derrop.proxy.api.events.connection.service.entity.EntityMoveEvent;
import com.github.derrop.proxy.api.events.connection.service.playerinfo.PlayerInfoAddEvent;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.events.GommeServerSwitchEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.cores.CoreJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.cores.CoreLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndDisconnectedEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndLeftEvent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GommeMatchListener {

    private int[] beaconStates;
    private int[] bedStates;

    private final MatchManager matchManager;

    public GommeMatchListener(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Listener
    public void handlePlayerConnect(PlayerServiceSelectedEvent event) {
        MatchInfo matchInfo = this.matchManager.getMatch(event.getConnection());
        if (matchInfo != null) {
            this.matchManager.showMatchData(event.getPlayer(), matchInfo);
        }
    }

    @Listener
    public void handlePlayerAdd(PlayerInfoAddEvent event) {
        MatchInfo matchInfo = this.matchManager.getMatch(event.getConnection());
        if (matchInfo == null) {
            return;
        }

        matchInfo.getPlayers().removeIf(playerInfo -> playerInfo.getUniqueId().equals(event.getPlayerInfo().getUniqueId()));
        matchInfo.getPlayers().add(event.getPlayerInfo());
    }

    @Listener
    public void handleDisconnect(ServiceDisconnectEvent event) {
        this.matchManager.deleteMatch(event.getConnection(), new MatchEndDisconnectedEvent());
    }

    @Listener
    public void handleMatchJoin(GommeServerSwitchEvent event) {
        ServiceConnection connection = event.getConnection();

        this.matchManager.deleteMatch(connection, new MatchEndLeftEvent());

        if (this.matchManager.getMatch(connection) != null) {
            return;
        }

        GommeServerType serverType = event.getServerType();
        if (serverType == GommeServerType.LOBBY) {
            return;
        }

        this.matchManager.createMatch(new MatchInfo(this.matchManager, connection, serverType, event.getMatchId()));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            System.out.println("MatchBegin on " + serverType + ": " + Arrays.stream(event.getConnection().getWorldDataProvider().getOnlinePlayers()).map(playerInfo -> playerInfo.getUniqueId() + "#" + playerInfo.getUsername()).collect(Collectors.joining(", ")));
        });
    }

    @Listener
    public void handleMove(EntityMoveEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();

        PlayerInfo playerInfo = player.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }

        this.handleMove(event.getConnection(), playerInfo, event.getFrom(), event.getTo());
    }

    @Listener
    public void handleMove(PlayerMoveEvent event) {
        ServiceConnection connection = event.getPlayer().getConnectedClient();
        if (connection == null) {
            return;
        }
        PlayerInfo playerInfo = connection.getWorldDataProvider().getOnlinePlayer(event.getPlayer().getUniqueId());
        if (playerInfo == null) {
            return;
        }

        this.handleMove(connection, playerInfo, event.getFrom(), event.getTo());
    }

    private void handleMove(ServiceConnection connection, PlayerInfo playerInfo, Location from, Location to) {
        MatchInfo match = this.matchManager.getMatch(connection);
        if (match == null) {
            return;
        }
        GommeServerType mode = match.getGameMode();

        if (mode != GommeServerType.CORES && mode != GommeServerType.CWCORES && mode != GommeServerType.BED_WARS && mode != GommeServerType.CWBW) {
            return;
        }
        boolean cores = mode == GommeServerType.CORES || mode == GommeServerType.CWCORES;

        if (cores && this.beaconStates == null) {
            this.beaconStates = connection.getBlockAccess().getBlockStateRegistry().getValidBlockStateIDs(Material.BEACON);
        } else if (!cores && this.bedStates == null) {
            this.bedStates = Arrays.stream(connection.getBlockAccess().getBlockStateRegistry().getValidStates(Material.BED_BLOCK))
                    .filter(blockState -> blockState.getHalf() == VerticalHalf.TOP)
                    .mapToInt(BlockState::getId)
                    .toArray();
        }

        Collection<Location> locations = connection.getBlockAccess().getPositions(cores ? this.beaconStates : this.bedStates);

        if (locations.isEmpty()) {
            return;
        }

        double maxDistanceSq = cores ? GommeConstants.CORE_NEARBY_DISTANCE_SQ : GommeConstants.BED_NEARBY_DISTANCE_SQ;
        for (Location loc : locations) {
            boolean toNearby = loc.distanceSquared(to) < maxDistanceSq;
            boolean fromNearby = loc.distanceSquared(from) < maxDistanceSq;

            // TODO this is not being called for other players

            if (fromNearby && !toNearby) {
                match.callEvent(cores ? new CoreLeaveEvent(playerInfo, loc) : new BedLeaveEvent(playerInfo, loc));
            } else if (toNearby && !fromNearby) {
                match.callEvent(cores ? new CoreJoinEvent(playerInfo, loc) : new BedJoinEvent(playerInfo, loc));
            }
        }
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || !event.getType().isChat()) {
            return;
        }

        MatchInfo match = this.matchManager.getMatch((ServiceConnection) event.getConnection());
        if (match == null) {
            return;
        }

        String message = ChatColor.stripColor(LegacyComponentSerializer.legacy().serialize(event.getMessage()));

        this.matchManager.getMessageRegistry().createMatchEvent(match.getSelectedLanguage(), match.getGameMode(), message).ifPresent(match::callEvent);
    }

}
