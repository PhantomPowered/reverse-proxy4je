package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageRegistry;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.RegisteredMessage;

import java.util.Map;

public class TeamRegistry extends MessageRegistry {

    public TeamRegistry() {
        super();

        super.registerMessage(Language.GERMAN, MessageType.TEAM_BLACK, "Schwarz", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_BLUE, "Blau", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_TURQUOISE, "Türkis", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_DIAMOND, "Diamant", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_DARK_BLUE, "D-Blau", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_DARK_GRAY, "D-Grau", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_DARK_GREEN, "D-Grün", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_DARK_RED, "D-Rot", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_GRAY, "Grau", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_GREEN, "Grün", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_ORANGE, "Orange", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_PINK, "Pink", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_PURPLE, "Violett", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_RED, "Rot", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_WHITE, "Weiß", () -> null, GommeGameMode.values());
        super.registerMessage(Language.GERMAN, MessageType.TEAM_YELLOW, "Gelb", () -> null, GommeGameMode.values());
    }

    public MessageType getTeam(Language language, GommeGameMode gameMode, String name) {
        Map<MessageType, RegisteredMessage> messages = this.messages.get(language).get(gameMode);
        for (Map.Entry<MessageType, RegisteredMessage> entry : messages.entrySet()) {
            if (!entry.getValue().hasVariables() && entry.getValue().getMessageTester().test(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
