package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.match.MatchBeginEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game.SpecificGameMessageRegistrar;

public class BedWarsMessageGameBegin extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerMessage(Language.GERMAN_GERMANY, MessageType.GAME_BEGIN, "[BedWars] Das Spiel beginnt!", MatchBeginEvent::new);
        registrar.registerMessage(Language.GERMAN_AUSTRIA, MessageType.GAME_BEGIN, "[BedWars] Des Spü startet!", MatchBeginEvent::new);
    }
}
