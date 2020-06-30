package com.github.derrop.proxy.plugins.gommecw.listener;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.entity.EntityPlayerSpawnEvent;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.events.GommeMatchActionEvent;
import com.github.derrop.proxy.plugins.gomme.events.GommeMatchDetectEvent;
import com.github.derrop.proxy.plugins.gomme.events.GommeServerSwitchEvent;
import com.github.derrop.proxy.plugins.gomme.match.MatchAction;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gommecw.GommeCWPlugin;
import com.github.derrop.proxy.plugins.gommecw.event.GommeCWAddEvent;
import com.github.derrop.proxy.plugins.gommecw.event.GommeCWRemoveEvent;
import com.github.derrop.proxy.plugins.gommecw.image.Frame;
import com.github.derrop.proxy.plugins.gommecw.running.ClanWarTeam;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarInfo;
import com.github.derrop.proxy.plugins.gommecw.web.WebClanInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GommeCWMatchListener {

    private final GommeCWPlugin plugin;
    private final Gson gson = new Gson();

    public GommeCWMatchListener(GommeCWPlugin plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void handleMatchBegin(GommeMatchDetectEvent event) {
        // TODO on match begin
        MatchInfo matchInfo = event.getMatchInfo();

        /*this.plugin.getWebParser().getCachedRunningInfo(matchInfo.getMatchId()) TODO remove or fix?
                .ifPresent(info -> {*/
        if (!this.plugin.getCwManager().isRegistered(matchInfo.getMatchId())) {
            this.plugin.getCwManager().register(event.getConnection(), new RunningClanWar(new RunningClanWarInfo(new WebClanInfo[]{new WebClanInfo("X"), new WebClanInfo("Y")}), matchInfo));
        }
        //      });
    }

    @Listener
    public void handlePlayerSpawn(EntityPlayerSpawnEvent event) {
        this.plugin.getCwManager().getClanWar(event.getConnection()).ifPresent(clanWar -> {
            if (clanWar.getTeams().stream().noneMatch(team -> team.getBedLocation() == null)) {
                return;
            }
            this.plugin.getCwManager().loadBedLocations(event.getConnection(), clanWar);
        });
    }

    @Listener
    public void handleCWdd(GommeCWAddEvent event) {
        WebClanInfo[] clans = event.getInfo().getClans();

        ServiceConnection connection = this.plugin.getCwManager().findRandomSpectator();
        if (connection == null) {
            System.err.printf("CW (%s vs. %s) detected on the map %s, but no free spectator could be found. MatchId: %s", clans[0].getName(), clans[1].getName(), event.getInfo().getMap(), event.getInfo().getMatchId());
            return;
        }

        this.plugin.getCwManager().jump(connection, event.getInfo());
    }

    @Listener
    public void handleFillTeam(GommeMatchActionEvent event) {
        if (event.getAction() != MatchAction.FILL_TEAM) {
            return;
        }

        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(event.getMatchInfo().getMatchId());
        if (clanWar == null) {
            return;
        }

        JsonObject data = event.getData();
        ClanWarTeam team = this.gson.fromJson(data, ClanWarTeam.class);

        boolean first = clanWar.getTeams().size() == 1;

        clanWar.getTeams().removeIf(e -> e.getColor() == team.getColor());
        clanWar.getTeams().add(team);

        if (first) {
            clanWar.setFrame(new Frame(clanWar));
            clanWar.getFrame().setVisible(true);
            clanWar.loadLabyUsers(event.getConnection().getProxy().getServiceRegistry());
        } else {
            this.plugin.getCwManager().loadBedLocations(event.getConnection(), clanWar);
        }
    }

    @Listener
    public void handleSyncTimer(GommeMatchActionEvent event) {
        if (event.getAction() != MatchAction.SYNC_TIMER) {
            return;
        }

        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(event.getMatchInfo().getMatchId());
        if (clanWar == null) {
            return;
        }

        int time = event.getData().get("time").getAsInt();
        clanWar.getInfo().setBeginTimestamp(System.currentTimeMillis() - (time * 1000));
    }

    @Listener
    public void handleStateUpdate(GommeMatchActionEvent event) {
        // TODO move when state is updated to ingame, not directly on connect
    }

    @Listener
    public void handleCWRemove(GommeCWRemoveEvent event) {
        this.plugin.getCwManager().unregister(event.getInfo().getMatchId());
    }

    @Listener
    public void handleServerSwitch(GommeServerSwitchEvent event) {
        ServiceConnection connection = event.getConnection();

        MatchInfo matchInfo = connection.getProxy().getServiceRegistry().getProviderUnchecked(GommeStatsCore.class).getMatchManager().getMatch(connection);
        if (matchInfo == null) {
            return;
        }
        RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(matchInfo.getMatchId());
        if (clanWar == null) {
            return;
        }
        /* TODO clanWar.getOurSpectators().remove(connection);
        if (clanWar.getOurSpectators().isEmpty()) {
            this.plugin.getCwManager().unregister(matchInfo.getMatchId());
        }*/

        if (clanWar.getFrame() != null) {
            clanWar.getFrame().dispose();
        }
    }

}
