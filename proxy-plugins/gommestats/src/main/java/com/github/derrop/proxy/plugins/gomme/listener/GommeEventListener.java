package com.github.derrop.proxy.plugins.gomme.listener;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.service.ServiceDisconnectEvent;
import com.github.derrop.proxy.api.network.ByteBufUtils;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.events.GommeMatchActionEvent;
import com.github.derrop.proxy.plugins.gomme.events.GommeServerSwitchEvent;
import com.github.derrop.proxy.plugins.gomme.match.MatchAction;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class GommeEventListener {

    private final MatchManager matchManager;

    public GommeEventListener(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Listener
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || !event.getTag().equals("GoMod")) {
            return;
        }

        ServiceConnection connection = (ServiceConnection) event.getConnection();
        ByteBuf buf = Unpooled.wrappedBuffer(event.getData());

        JsonObject jsonObject = JsonParser.parseString(ByteBufUtils.readString(buf)).getAsJsonObject();

        String action = jsonObject.get("action").getAsString().toUpperCase();
        JsonObject data = jsonObject.has("data") ? jsonObject.get("data").getAsJsonObject() : null;
        if (data == null) {
            return;
        }

        if (action.equals("JOIN_SERVER")) {
            String serverType = data.get("cloud_type").getAsString().toUpperCase();
            String matchId = data.get("id").getAsString();

            GommeServerType type = GommeServerType.getByGommeName(serverType);
            if (type == null) {
                return;
            }

            connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                    .callEvent(new GommeServerSwitchEvent(connection, matchId, type));

            connection.setProperty(GommeConstants.CURRENT_SERVER_PROPERTY, type);
        } else {
            MatchInfo matchInfo = this.matchManager.getMatch(connection);
            if (matchInfo == null) {
                return;
            }

            try {
                MatchAction parsedAction = MatchAction.valueOf(action);
                connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                        .callEvent(new GommeMatchActionEvent(connection, matchInfo, parsedAction, data));
            } catch (IllegalArgumentException exception) {
            }
        }
    }

    @Listener
    public void handleServiceDisconnect(ServiceDisconnectEvent event) {
        event.getConnection().removeProperty(GommeConstants.CURRENT_SERVER_PROPERTY);
    }

}
