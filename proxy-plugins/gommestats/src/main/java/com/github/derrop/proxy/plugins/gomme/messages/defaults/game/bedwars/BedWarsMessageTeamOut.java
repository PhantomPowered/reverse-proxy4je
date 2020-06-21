package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.TeamOutEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageTeamOut extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.TEAM_OUT, "\\[BedWars] Team (.*) wurde vernichtet!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1)),
                map -> new TeamOutEvent(registrar.getTeam(Language.GERMANY, map.get("team")))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.TEAM_OUT, "\\[BedWars] Team (.*) is vanichtet wordn!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1)),
                map -> new TeamOutEvent(registrar.getTeam(Language.AUSTRIA, map.get("team")))
        );
    }
}
