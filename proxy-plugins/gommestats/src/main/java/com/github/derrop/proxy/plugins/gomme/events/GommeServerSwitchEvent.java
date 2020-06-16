package com.github.derrop.proxy.plugins.gomme.events;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import org.jetbrains.annotations.NotNull;

public class GommeServerSwitchEvent extends Event {

    private final ServiceConnection connection;
    private final String matchId;
    private final GommeGameMode gameMode;

    public GommeServerSwitchEvent(@NotNull ServiceConnection connection, @NotNull String matchId, @NotNull GommeGameMode gameMode) {
        this.connection = connection;
        this.matchId = matchId;
        this.gameMode = gameMode;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public String getMatchId() {
        return this.matchId;
    }

    @NotNull
    public GommeGameMode getGameMode() {
        return this.gameMode;
    }
}
