package com.github.derrop.proxy.plugins.gomme.secret;

import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUpdateEvent;

public class GommeNickDetector {

    // TODO so it seems like Gomme sends a scoreboard team update containing the nicked players real name in the entries on join, maybe we could check whether the team gets unregistered/updated?
    //  if not, it might be a possibility to check whether the player is still in the tablist like 500 ms later AND no leave message was sent

    @Listener
    public void handleScoreboardTeamUpdate(ScoreboardTeamUpdateEvent event) {
    }

}
