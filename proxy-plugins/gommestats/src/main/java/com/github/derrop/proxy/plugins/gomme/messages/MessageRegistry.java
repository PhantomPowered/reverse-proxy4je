package com.github.derrop.proxy.plugins.gomme.messages;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRegistry {

    protected final Map<Language, Map<GommeServerType, Map<MessageType, RegisteredMessage>>> messages = new HashMap<>();

    public MessageRegistry() {
        for (Language language : Language.values()) {
            Map<GommeServerType, Map<MessageType, RegisteredMessage>> messagesForLanguage = new HashMap<>();
            for (GommeServerType gameMode : GommeServerType.values()) {
                messagesForLanguage.put(gameMode, new HashMap<>());
            }
            messages.put(language, messagesForLanguage);
        }
    }

    public void registerMessage(Language language, MessageType type, String message, Supplier<MatchEvent> matchEventMapper, GommeServerType... gameModes) {
        RegisteredMessage registeredMessage = new RegisteredMessage(input -> input.equals(message), map -> matchEventMapper.get());
        
        Map<GommeServerType, Map<MessageType, RegisteredMessage>> messagesForLanguage = messages.get(language);
        for (GommeServerType gameMode : gameModes) {
            messagesForLanguage.get(gameMode).put(type, registeredMessage);
        }
    }

    public void registerRegExMessage(Language language, MessageType type, String regex,
                                             BiFunction<String, Matcher, Map<String, String>> variablesMapper,
                                             Function<Map<String, String>, MatchEvent> matchEventMapper,
                                             GommeServerType... gameModes) {
        Pattern pattern = Pattern.compile(regex);
        RegisteredMessage registeredMessage = new RegisteredMessage(
                input -> pattern.matcher(input).matches(),
                input -> {
                    Matcher matcher = pattern.matcher(input);
                    if (!matcher.find()) {
                        throw new IllegalStateException("Wrong pattern provided for message: '" + input + "' (RegEx: '" + regex + "')");
                    }
                    return variablesMapper.apply(input, matcher);
                },
                matchEventMapper
        );

        Map<GommeServerType, Map<MessageType, RegisteredMessage>> messagesForLanguage = messages.get(language);
        for (GommeServerType gameMode : gameModes) {
            messagesForLanguage.get(gameMode).put(type, registeredMessage);
        }
    }

    public Optional<RegisteredMessage> getMessage(Language language, GommeServerType gameMode, String message) {
        Map<MessageType, RegisteredMessage> messages = this.messages.get(language).get(gameMode);
        for (Map.Entry<MessageType, RegisteredMessage> entry : messages.entrySet()) {
            if (entry.getValue().getMessageTester().test(message)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Map<Language, Map<GommeServerType, Map<MessageType, RegisteredMessage>>> getMessages() {
        return this.messages;
    }
}
