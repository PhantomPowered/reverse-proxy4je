package com.github.derrop.proxy.plugins.gomme.events;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import org.jetbrains.annotations.NotNull;

public class GommeMatchDetectEvent extends Event {

    private final ServiceConnection connection;
    private final MatchInfo matchInfo;

    public GommeMatchDetectEvent(@NotNull ServiceConnection connection, @NotNull MatchInfo matchInfo) {
        this.connection = connection;
        this.matchInfo = matchInfo;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public MatchInfo getMatchInfo() {
        return this.matchInfo;
    }
}
