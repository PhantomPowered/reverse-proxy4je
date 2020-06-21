package com.github.derrop.proxy.plugins.gomme.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GameMessageRegistry extends MessageRegistry {

    public GameMessageRegistry(TeamRegistry teamRegistry) {
        super();

        String packageName = super.getClass().getPackage().getName() + ".";

        Collection<SpecificGameMessageRegistrar> registrars = new ArrayList<>();
        registrars.add(new ReflectiveGameMessageRegistrar(packageName + "bedwars", GommeServerType.BED_WARS, this, teamRegistry));

        for (SpecificGameMessageRegistrar registrar : registrars) {
            registrar.init();
        }
    }

    public Optional<MatchEvent> createMatchEvent(Language language, GommeServerType gameMode, String message) {
        return super.getMessage(language, gameMode, message).map(registeredMessage -> registeredMessage.createMatchEvent(message));
    }

}
