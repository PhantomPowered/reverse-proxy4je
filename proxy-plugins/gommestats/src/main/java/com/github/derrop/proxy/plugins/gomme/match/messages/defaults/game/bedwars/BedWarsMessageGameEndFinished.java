package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndFinishedEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageGameEndFinished extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMAN_GERMANY, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hat gewonnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(registrar.getTeam(Language.GERMAN_GERMANY, map.get("winner")))
        );
        registrar.registerRegExMessage(Language.GERMAN_AUSTRIA, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hot gwunnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(registrar.getTeam(Language.GERMAN_AUSTRIA, map.get("winner")))
        );
    }
}
