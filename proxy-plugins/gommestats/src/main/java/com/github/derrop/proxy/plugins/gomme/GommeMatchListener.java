package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class GommeMatchListener {

    private final MatchManager matchManager;

    public GommeMatchListener(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Listener
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || !event.getTag().equals("GoMod")) {
            return;
        }

        ByteBuf buf = Unpooled.wrappedBuffer(event.getData());

        JsonObject jsonObject = JsonParser.parseString(ByteBufUtils.readString(buf)).getAsJsonObject();

        String action = jsonObject.get("action").getAsString().toUpperCase();
        JsonObject data = jsonObject.has("data") ? jsonObject.get("data").getAsJsonObject() : null;

        if (data != null && action.equals("JOIN_SERVER")) {
            String serverType = data.get("cloud_type").getAsString().toUpperCase();
            String matchId = data.get("id").getAsString();

            this.matchManager.deleteMatch((ServiceConnection) event.getConnection());

            if (this.matchManager.getMatch(matchId) != null) {
                return;
            }

            GommeGameMode gameMode = GommeGameMode.getByGommeName(serverType);
            if (gameMode == null) {
                return;
            }

            this.matchManager.createMatch(new MatchInfo(
                    (ServiceConnection) event.getConnection(),
                    gameMode,
                    matchId
            ));
        }
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT) {
            return;
        }

        MatchInfo match = this.matchManager.getMatch((ServiceConnection) event.getConnection());
        if (match == null) {
            return;
        }

        // TODO this message could be the one to start a round, a core could have been destroyed (better do that with the scoreboard?)
    }

}
