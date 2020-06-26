package com.github.derrop.proxy.plugins.gomme.events;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.plugins.gomme.match.MatchAction;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class GommeMatchActionEvent extends Event {

    private final ServiceConnection connection;
    private final MatchInfo matchInfo;
    private final MatchAction action;
    private final JsonObject data;

    public GommeMatchActionEvent(@NotNull ServiceConnection connection, @NotNull MatchInfo matchInfo, @NotNull MatchAction action, @NotNull JsonObject data) {
        this.connection = connection;
        this.matchInfo = matchInfo;
        this.action = action;
        this.data = data;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public MatchInfo getMatchInfo() {
        return this.matchInfo;
    }

    @NotNull
    public MatchAction getAction() {
        return this.action;
    }

    @NotNull
    public JsonObject getData() {
        return this.data;
    }
}
