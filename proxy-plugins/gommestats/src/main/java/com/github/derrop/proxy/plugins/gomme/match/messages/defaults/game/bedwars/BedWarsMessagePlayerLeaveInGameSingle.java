package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerLeaveInGameSingleEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerLeaveInGameSingle extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMAN_GERMANY, MessageType.PLAYER_LEAVE_IN_GAME_SINGLE,
                "\\[BedWars] (.*) hat das Spiel verlassen",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveInGameSingleEvent(map.get("player"))
        );
        registrar.registerRegExMessage(Language.GERMAN_AUSTRIA, MessageType.PLAYER_LEAVE_IN_GAME_SINGLE,
                "\\[BedWars] (.*) hots Spü valossn",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveInGameSingleEvent(map.get("player"))
        );
    }
}
