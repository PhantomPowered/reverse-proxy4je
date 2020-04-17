package com.github.derrop.proxy.plugins.gomme.match;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.player.PlayerData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MatchManager {

    private final GommeStatsCore core;

    public MatchManager(GommeStatsCore core) {
        this.core = core;
    }

    private final Map<String, MatchInfo> openMatches = new ConcurrentHashMap<>();

    public Collection<MatchInfo> getRunningMatches() {
        return this.openMatches.values().stream().filter(MatchInfo::isRunning).collect(Collectors.toList());
    }

    public Collection<MatchInfo> getRunningMatches(GommeGameMode gameMode) {
        return this.getRunningMatches().stream().filter(matchInfo -> matchInfo.getGameMode() == gameMode).collect(Collectors.toList());
    }

    public MatchInfo getMatch(String matchId) {
        return this.openMatches.get(matchId);
    }

    public MatchInfo getMatch(ServiceConnection connection) {
        for (MatchInfo value : this.openMatches.values()) {
            if (value.getInvoker().equals(connection) || Arrays.stream(value.getInvoker().getWorldDataProvider().getOnlinePlayers())
                    .anyMatch(playerInfo -> playerInfo.getUniqueId().equals(connection.getUniqueId()))) {
                return value;
            }
        }
        return null;
    }

    public void createMatch(MatchInfo matchInfo) {
        this.openMatches.put(matchInfo.getMatchId(), matchInfo);
    }

    public void deleteMatch(ServiceConnection invoker) {
        for (MatchInfo value : this.openMatches.values()) {
            if (value.getInvoker().equals(invoker)) {
                this.openMatches.remove(value.getMatchId());
                if (value.hasEnded()) {
                    this.writeToDatabase(value);
                }
            }
        }
    }

    public void startMatch(String matchId) {
        MatchInfo matchInfo = this.openMatches.get(matchId);
        if (matchInfo != null) {
            PlayerInfo[] players = matchInfo.getInvoker().getWorldDataProvider().getOnlinePlayers();
            PlayerData[] statistics = new PlayerData[players.length];
            for (int i = 0; i < players.length; i++) {
                statistics[i] = this.core.getPlayerDataProvider().getData(players[i].getUniqueId());
            }
            matchInfo.start(players, statistics);
        }
    }

    public void endMatch(String matchId) {
        MatchInfo matchInfo = this.openMatches.remove(matchId);
        if (matchInfo != null) {
            matchInfo.end(matchInfo.getInvoker().getWorldDataProvider().getOnlinePlayers());
            this.writeToDatabase(matchInfo);
        }
    }

    private void writeToDatabase(MatchInfo matchInfo) {
        // TODO
    }

    public long countMatches() {
        return -1;
    }

    public long countMatches(GommeGameMode gameMode) {
        return -1;
    }

    public Collection<MatchInfo> getPastMatches() {
        return null;
    }

}
