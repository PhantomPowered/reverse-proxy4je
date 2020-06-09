package com.github.derrop.proxy.plugins.gomme.match.messages;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.event.*;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedDestroyEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedWarsTryDestroyOwnBedEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.*;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.*;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRegistry {

    private static final Map<Language, Map<GommeGameMode, Map<MessageType, RegisteredMessage>>> MESSAGES = new HashMap<>();

    static {
        for (Language language : Language.values()) {
            Map<GommeGameMode, Map<MessageType, RegisteredMessage>> messagesForLanguage = new HashMap<>();
            for (GommeGameMode gameMode : GommeGameMode.values()) {
                messagesForLanguage.put(gameMode, new HashMap<>());
            }
            MESSAGES.put(language, messagesForLanguage);
        }


        registerMessage(Language.GERMAN, MessageType.GAME_BEGIN, "[BedWars] Das Spiel beginnt!", map -> new MatchBeginEvent(), GommeGameMode.BED_WARS);
        registerMessage(Language.GERMAN, MessageType.GAME_END_DIED, "[BedWars] Du bist nun Zuschauer!", map -> new MatchEndDiedEvent(), GommeGameMode.BED_WARS);
        registerRegExMessage(Language.GERMAN, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hat gewonnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(map.get("winner")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.MAP_SELECTED, "\\[BedWars] Map: (.*) von: (.*)",
                (input, matcher) -> ImmutableMap.of("map", matcher.group(1), "builder", matcher.group(2)),
                map -> new MapSelectedEvent(map.get("map"), map.get("builder")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_JOIN_LOBBY, "» (.*) hat das Spiel betreten",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerJoinLobbyEvent(map.get("player")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_LEAVE_LOBBY, "« (.*) hat das Spiel verlassen",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveLobbyEvent(map.get("player")),
                GommeGameMode.BED_WARS
        );
        registerMessage(Language.GERMAN, MessageType.CANNOT_DESTROY_OWN_BED, "[BedWars] Du kannst dein eigenes Bett nicht zerstören!", map -> new BedWarsTryDestroyOwnBedEvent(), GommeGameMode.BED_WARS);
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_DIED,
                "\\[BedWars] (.*) ist gestorben",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerDiedEvent(map.get("player")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_KILLED,
                "\\[BedWars] (.*) wurde von (.*) getötet",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "killer", matcher.group(2)),
                map -> new PlayerKilledEvent(map.get("player"), map.get("killer")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_LEAVE_IN_GAME,
                "\\[BedWars] (.*) hat das Spiel verlassen. Team (.*) hat noch (\\d+) Spieler",
                (input, matcher) ->
                        ImmutableMap.of("player", matcher.group(1), "team", matcher.group(2), "remaining", matcher.group(3)),
                map -> new PlayerLeaveInGameEvent(map.get("player"), map.get("team"), Integer.parseInt(map.get("remaining"))),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.BED_DESTROYED,
                "\\[BedWars] Das Bett von Team (.*) wurde von (.*) zerstört!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1), "destroyer", matcher.group(2)),
                map -> new BedDestroyEvent(map.get("destroyer"), map.get("team")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.TEAM_OUT, "\\[BedWars] Team (.*) wurde vernichtet!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1)),
                map -> new TeamOutEvent(map.get("team")),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_TEAM_CHAT_MESSAGE, "\\[@all\\] (?:\\S+ )?(\\S+): (.*)",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "message", matcher.group(2)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), true),
                GommeGameMode.BED_WARS
        );
        registerRegExMessage(Language.GERMAN, MessageType.PLAYER_GLOBAL_CHAT_MESSAGE, "(?:\\S+ )?(\\S+): (.*)",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "message", matcher.group(2)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), false),
                GommeGameMode.BED_WARS
        );

        // TODO team names should be parsed by the language into an enum
    }

    private static void registerMessage(Language language, MessageType type, String message, Function<Map<String, String>, MatchEvent> matchEventMapper,GommeGameMode... gameModes) {
        RegisteredMessage registeredMessage = new RegisteredMessage(input -> input.equals(message), matchEventMapper);

        Map<GommeGameMode, Map<MessageType, RegisteredMessage>> messagesForLanguage = MESSAGES.get(language);
        for (GommeGameMode gameMode : gameModes) {
            messagesForLanguage.get(gameMode).put(type, registeredMessage);
        }
    }

    private static void registerRegExMessage(Language language, MessageType type, String regex,
                                             BiFunction<String, Matcher, Map<String, String>> variablesMapper,
                                             Function<Map<String, String>, MatchEvent> matchEventMapper,
                                             GommeGameMode... gameModes) {
        Pattern pattern = Pattern.compile(regex);
        RegisteredMessage registeredMessage = new RegisteredMessage(
                input -> pattern.matcher(input).matches(),
                input -> {
                    Matcher matcher = pattern.matcher(input);
                    if (!matcher.find()) {
                        throw new IllegalStateException("Wrong pattern provided for message: '" + input + "' (ReGex: '" + regex + "')");
                    }
                    return variablesMapper.apply(input, matcher);
                },
                matchEventMapper
        );

        Map<GommeGameMode, Map<MessageType, RegisteredMessage>> messagesForLanguage = MESSAGES.get(language);
        for (GommeGameMode gameMode : gameModes) {
            messagesForLanguage.get(gameMode).put(type, registeredMessage);
        }
    }

    public static Optional<RegisteredMessage> getMessage(Language language, GommeGameMode gameMode, String message) {
        Map<MessageType, RegisteredMessage> messages = MESSAGES.get(language).get(gameMode);
        for (Map.Entry<MessageType, RegisteredMessage> entry : messages.entrySet()) {
            if (entry.getValue().getMessageTester().test(message)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public static Optional<MatchEvent> createMatchEvent(Language language, GommeGameMode gameMode, String message) {
        return getMessage(language, gameMode, message).map(registeredMessage -> registeredMessage.createMatchEvent(message));
    }

}
