package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MapSelectedEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageMapSelected extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.MAP_SELECTED, "\\[BedWars] Map: (.*) von: (.*)",
                (input, matcher) -> ImmutableMap.of("map", matcher.group(1), "builder", matcher.group(2)),
                map -> new MapSelectedEvent(map.get("map"), map.get("builder"))
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.MAP_SELECTED, "\\[BedWars] Map: (.*) von (.*)",
                (input, matcher) -> ImmutableMap.of("map", matcher.group(1), "builder", matcher.group(2)),
                map -> new MapSelectedEvent(map.get("map"), map.get("builder"))
        );
    }
}
