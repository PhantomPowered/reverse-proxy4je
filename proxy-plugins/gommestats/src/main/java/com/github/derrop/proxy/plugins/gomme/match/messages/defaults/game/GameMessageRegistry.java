package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GameMessageRegistry extends MessageRegistry {

    public GameMessageRegistry(TeamRegistry teamRegistry) {
        super();

        String packageName = super.getClass().getPackage().getName() + ".";

        Collection<SpecificGameMessageRegistrar> registrars = new ArrayList<>();
        registrars.add(new ReflectiveGameMessageRegistrar(packageName + "bedwars", GommeGameMode.BED_WARS, this, teamRegistry));

        for (SpecificGameMessageRegistrar registrar : registrars) {
            registrar.init();
        }
    }

    public Optional<MatchEvent> createMatchEvent(Language language, GommeGameMode gameMode, String message) {
        return super.getMessage(language, gameMode, message).map(registeredMessage -> registeredMessage.createMatchEvent(message));
    }

}
