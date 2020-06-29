package com.github.derrop.proxy.plugins.gommecw.running;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.clan.ClanInfo;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RunningClanWarManager {

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
