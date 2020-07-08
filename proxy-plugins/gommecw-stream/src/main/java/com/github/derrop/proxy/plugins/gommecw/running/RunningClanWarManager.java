package com.github.derrop.proxy.plugins.gommecw.running;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.block.BlockState;
import com.github.derrop.proxy.api.block.Half;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.clan.ClanInfo;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RunningClanWarManager {

    private static final double MAX_BED_DISTANCE_SQ = 15 * 15;

    private final ServiceRegistry registry;
    private final Map<String, RunningClanWar> clanWars = new ConcurrentHashMap<>();

    public RunningClanWarManager(ServiceRegistry registry) {
        this.registry = registry;
    }

    public Optional<RunningClanWar> getClanWar(ServiceConnection spectator) {
        return this.clanWars.values().stream().filter(clanWar -> clanWar.getOurSpectators().contains(spectator)).findFirst();
    }

    public RunningClanWar getClanWar(String matchId) {
        return this.clanWars.get(matchId);
    }

    public Collection<RunningClanWar> getClanWars() {
        return this.clanWars.values();
    }

    public boolean isRegistered(String matchId) {
        return this.clanWars.containsKey(matchId);
    }

    public void unregister(String matchId) {
        RunningClanWar clanWar = this.clanWars.remove(matchId);
        if (clanWar != null) {
            for (ServiceConnection ourSpectator : clanWar.getOurSpectators()) {
                ourSpectator.chat("/hub");
            }
        }
    }

    public void register(ServiceConnection connection, RunningClanWar clanWar) {
        Preconditions.checkArgument(!this.isRegistered(clanWar.getMatchInfo().getMatchId()), "A ClanWar with that ID is already registered");
        this.clanWars.put(clanWar.getMatchInfo().getMatchId(), clanWar);

        this.initSpectator(connection, clanWar, 0);
        clanWar.getOurSpectators().add(connection);

        for (int i = 0; i < 2; i++) {
            ServiceConnection spectator = this.findRandomSpectator();
            if (spectator == null) {
                break;
            }
            this.jump(spectator, clanWar.getInfo());
            this.initSpectator(spectator, clanWar, i + 1);
            clanWar.getOurSpectators().add(spectator);
        }
    }

    public void loadBedLocations(ServiceConnection connection, RunningClanWar clanWar) {
        if (clanWar.getTeams().stream().noneMatch(ClanWarTeam::isBedAlive)) {
            return;
        }
        // TODO don't use only one ServiceConnection to get the bed locations but use all

        Collection<Location> beds = connection.getBlockAccess().getPositions(Material.BED_BLOCK);
        if (beds.size() != 4) {
            UUID trackerId = UUID.randomUUID();
            clanWar.getBlockStateTrackerIds().add(trackerId);
            connection.getBlockAccess().trackBlockUpdates(trackerId, Material.BED_BLOCK, (x, y, z, oldState, state) -> {
                if (this.isBed(connection, state)) {
                    this.updateBedLocation(connection, clanWar, new Location(x, y, z));
                }
            });
            return;
        }

        for (Location bed : beds) {
            int state = connection.getBlockAccess().getBlockState(bed);
            if (this.isBed(connection, state)) {
                this.updateBedLocation(connection, clanWar, bed);
            }
        }
    }

    private boolean isBed(ServiceConnection connection, int state) {
        BlockState exactState = connection.getBlockAccess().getBlockStateRegistry().getExactBlockState(state);
        return exactState.getMaterial() == Material.BED_BLOCK && exactState.getHalf() == Half.TOP;
    }

    private void updateBedLocation(ServiceConnection connection, RunningClanWar clanWar, Location bedLocation) {
        if (clanWar.getTeams().stream().anyMatch(team -> bedLocation.equals(team.getBedLocation()))) {
            return;
        }

        connection.getWorldDataProvider().getPlayersInWorld().stream()
                .filter(player -> {
                    ClanWarTeam team = clanWar.getTeam(player.getUniqueId());
                    return team != null && team.getBedLocation() == null;
                })
                .filter(player -> player.getLocation().distanceSquared(bedLocation) < MAX_BED_DISTANCE_SQ)
                .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(bedLocation)))
                .ifPresent(player -> {
                    ClanWarTeam team = clanWar.getTeam(player.getUniqueId());
                    if (team != null) {
                        team.setBedLocation(bedLocation);
                    }
                });

        if (clanWar.getTeams().stream().noneMatch(team -> team.getBedLocation() == null)) {
            for (UUID trackerId : clanWar.getBlockStateTrackerIds()) {
                connection.getBlockAccess().untrackBlockUpdates(trackerId);
            }
        }
    }

    private void initSpectator(ServiceConnection connection, RunningClanWar clanWar, int index) {
        Constants.SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            connection.getAbilities().setFlying(true);

            // TODO move into position

        }, 100, TimeUnit.MILLISECONDS);
    }

    public void jump(ServiceConnection connection, RunningClanWarInfo info) {
        // TODO make sure that a connection doesn't try to connect with multiple CWs within a few milliseconds
        ClanInfo clanInfo = this.registry.getProviderUnchecked(GommeStatsCore.class).getClanInfoProvider().getActualClan(connection, info.getClans()[0].getName());
        if (clanInfo == null) {
            return;
        }
        connection.chat("/cw join " + clanInfo.getShortcut());
    }

    public ServiceConnection findRandomSpectator() {
        Collection<? extends ServiceConnection> clients = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients();
        ServiceConnection result;
        do {
            result = clients.isEmpty() ? null : clients.iterator().next();
        } while (result != null && result.getProperty(GommeConstants.CURRENT_SERVER_PROPERTY) != GommeServerType.LOBBY);
        return result;
    }

}
