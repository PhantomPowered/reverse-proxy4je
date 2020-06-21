package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerJoinLobbyEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerJoinLobby extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.PLAYER_JOIN_LOBBY, "» (.*) hat das Spiel betreten",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerJoinLobbyEvent(map.get("player"))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.PLAYER_JOIN_LOBBY, "» (.*) hots Spü betretn",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerJoinLobbyEvent(map.get("player"))
        );
    }
}
