package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedDestroyEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageBedDestroyed extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMAN_GERMANY, MessageType.BED_DESTROYED,
                "\\[BedWars] Das Bett von Team (.*) wurde von (.*) zerstört!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1), "destroyer", matcher.group(2)),
                map -> new BedDestroyEvent(map.get("destroyer"), registrar.getTeam(Language.GERMAN_GERMANY, map.get("team")))
        );
        registrar.registerRegExMessage(Language.GERMAN_AUSTRIA, MessageType.BED_DESTROYED,
                "\\[BedWars] Des Bett von Team (.*) is von (.*) zastöat wordn!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1), "destroyer", matcher.group(2)),
                map -> new BedDestroyEvent(map.get("destroyer"), registrar.getTeam(Language.GERMAN_AUSTRIA, map.get("team")))
        );
    }
}
