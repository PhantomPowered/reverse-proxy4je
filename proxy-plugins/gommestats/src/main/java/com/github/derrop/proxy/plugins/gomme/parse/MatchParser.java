package com.github.derrop.proxy.plugins.gomme.parse;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;

public class MatchParser {

    private final GommeStatsCore core;

    public MatchParser(GommeStatsCore core) {
        this.core = core;
    }

    protected MatchInfo getMatchInfo(ServiceConnection connection) {
        return this.core.getMatchManager().getMatch(connection);
    }

}
