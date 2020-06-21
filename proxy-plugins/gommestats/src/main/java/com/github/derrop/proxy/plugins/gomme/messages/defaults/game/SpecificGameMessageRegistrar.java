package com.github.derrop.proxy.plugins.gomme.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public abstract class SpecificGameMessageRegistrar {

    private final GommeServerType gameMode;
    private final GameMessageRegistry registry;
    private final TeamRegistry teamRegistry;

    public SpecificGameMessageRegistrar(GommeServerType gameMode, GameMessageRegistry registry, TeamRegistry teamRegistry) {
        this.gameMode = gameMode;
        this.registry = registry;
        this.teamRegistry = teamRegistry;
    }

    public abstract void init();

    public void registerMessage(Language language, MessageType type, String message, Supplier<MatchEvent> matchEventMapper) {
        this.registry.registerMessage(language, type, message, matchEventMapper, this.gameMode);
    }

    public void registerRegExMessage(Language language, MessageType type, String regex,
                                     BiFunction<String, Matcher, Map<String, String>> variablesMapper,
                                     Function<Map<String, String>, MatchEvent> matchEventMapper) {
        this.registry.registerRegExMessage(language, type, regex, variablesMapper, matchEventMapper, this.gameMode);
    }

    public MessageType getTeam(Language language, String name) {
        return this.teamRegistry.getTeam(language, this.gameMode, name);
    }

}
