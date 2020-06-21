package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerLeaveInGameMultiEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerLeaveInGameMulti extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.PLAYER_LEAVE_IN_GAME_MULTI,
                "\\[BedWars] (.*) hat das Spiel verlassen. Team (.*) hat noch (\\d+) Spieler",
                (input, matcher) ->
                        ImmutableMap.of("player", matcher.group(1), "team", matcher.group(2), "remaining", matcher.group(3)),
                map -> new PlayerLeaveInGameMultiEvent(map.get("player"), registrar.getTeam(Language.GERMANY, map.get("team")), Integer.parseInt(map.get("remaining")))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.PLAYER_LEAVE_IN_GAME_MULTI,
                "\\[BedWars] (.*) hots Spü valossn. Team (.*) hot noch (\\d+) Spiela",
                (input, matcher) ->
                        ImmutableMap.of("player", matcher.group(1), "team", matcher.group(2), "remaining", matcher.group(3)),
                map -> new PlayerLeaveInGameMultiEvent(map.get("player"), registrar.getTeam(Language.AUSTRIA, map.get("team")), Integer.parseInt(map.get("remaining")))
        );
    }
}
