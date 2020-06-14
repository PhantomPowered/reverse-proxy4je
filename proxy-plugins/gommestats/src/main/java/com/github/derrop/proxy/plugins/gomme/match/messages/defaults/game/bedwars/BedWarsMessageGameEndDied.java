package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchEndDiedEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;

public class BedWarsMessageGameEndDied extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerMessage(Language.GERMAN_GERMANY, MessageType.GAME_END_DIED, "[BedWars] Du bist nun Zuschauer!", MatchEndDiedEvent::new);
        registrar.registerMessage(Language.GERMAN_AUSTRIA, MessageType.GAME_END_DIED, "[BedWars] Du bist jetz Zuschaua!", MatchEndDiedEvent::new);
    }
}
