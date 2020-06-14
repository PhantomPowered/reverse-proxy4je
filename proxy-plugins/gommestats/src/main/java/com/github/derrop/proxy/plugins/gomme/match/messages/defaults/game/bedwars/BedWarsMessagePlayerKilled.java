package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerKilledEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessagePlayerKilled extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMAN_GERMANY, MessageType.PLAYER_KILLED,
                "\\[BedWars] (.*) wurde von (.*) getötet",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "killer", matcher.group(2)),
                map -> new PlayerKilledEvent(map.get("player"), map.get("killer"))
        );
        registrar.registerRegExMessage(Language.GERMAN_AUSTRIA, MessageType.PLAYER_KILLED,
                "\\[BedWars] (.*) is von (.*) getötet wordn",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "killer", matcher.group(2)),
                map -> new PlayerKilledEvent(map.get("player"), map.get("killer"))
        );
    }
}
