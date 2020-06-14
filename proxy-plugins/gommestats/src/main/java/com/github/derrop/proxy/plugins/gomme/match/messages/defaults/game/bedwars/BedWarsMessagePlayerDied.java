package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerDiedEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerDied extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMAN_GERMANY, MessageType.PLAYER_DIED,
                "\\[BedWars] (.*) ist gestorben",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerDiedEvent(map.get("player"))
        );
        registrar.registerRegExMessage(Language.GERMAN_AUSTRIA, MessageType.PLAYER_DIED,
                "\\[BedWars] (.*) is gstorbn",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerDiedEvent(map.get("player"))
        );
    }
}
