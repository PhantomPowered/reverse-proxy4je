package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerLeaveLobbyEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerLeaveLobby extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.PLAYER_LEAVE_LOBBY, "« (.*) hat das Spiel verlassen",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveLobbyEvent(map.get("player"))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.PLAYER_LEAVE_LOBBY, "« (.*) hots Spü valossn",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveLobbyEvent(map.get("player"))
        );
    }
}
