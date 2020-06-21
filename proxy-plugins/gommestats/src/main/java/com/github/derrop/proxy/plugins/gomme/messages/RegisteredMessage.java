package com.github.derrop.proxy.plugins.gomme.messages;

import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegisteredMessage {

    private final Predicate<String> messageTester;
    private final boolean variables;
    private final Function<String, Map<String, String>> variablesMapper;
    private final Function<Map<String, String>, MatchEvent> matchEventMapper;

    public RegisteredMessage(Predicate<String> messageTester, Function<String, Map<String, String>> variablesMapper, Function<Map<String, String>, MatchEvent> matchEventMapper) {
        this.messageTester = messageTester;
        this.variables = true;
        this.variablesMapper = variablesMapper;
        this.matchEventMapper = matchEventMapper;
    }

    public RegisteredMessage(Predicate<String> messageTester, Function<Map<String, String>, MatchEvent> matchEventMapper) {
        this.messageTester = messageTester;
        this.variables = false;
        this.variablesMapper = null;
        this.matchEventMapper = matchEventMapper;
    }

    public Predicate<String> getMessageTester() {
        return this.messageTester;
    }

    public boolean hasVariables() {
        return this.variables;
    }

    public Function<String, Map<String, String>> getVariablesMapper() {
        return this.variablesMapper;
    }

    public Function<Map<String, String>, MatchEvent> getMatchEventMapper() {
        return this.matchEventMapper;
    }

    public MatchEvent createMatchEvent(String input) {
        return this.matchEventMapper.apply(this.hasVariables() && this.variablesMapper != null ? this.variablesMapper.apply(input) : null);
    }

}
