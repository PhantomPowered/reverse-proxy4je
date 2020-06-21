package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndFinishedEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageGameEndFinished extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hat gewonnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(registrar.getTeam(Language.GERMANY, map.get("winner")))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hot gwunnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(registrar.getTeam(Language.AUSTRIA, map.get("winner")))
        );
    }
}
