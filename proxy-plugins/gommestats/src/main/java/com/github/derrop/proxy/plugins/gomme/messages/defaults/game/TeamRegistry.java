package com.github.derrop.proxy.plugins.gomme.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageRegistry;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.RegisteredMessage;

import java.util.Map;

public class TeamRegistry extends MessageRegistry {

    public TeamRegistry() {
        super();

        super.registerMessage(Language.GERMANY, MessageType.TEAM_BLACK, "Schwarz", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_BLUE, "Blau", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_TURQUOISE, "Türkis", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_DIAMOND, "Diamant", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_DARK_BLUE, "D-Blau", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_DARK_GRAY, "D-Grau", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_DARK_GREEN, "D-Grün", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_DARK_RED, "D-Rot", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_GRAY, "Grau", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_GREEN, "Grün", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_ORANGE, "Orange", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_PINK, "Pink", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_PURPLE, "Violett", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_RED, "Rot", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_WHITE, "Weiß", () -> null, GommeServerType.values());
        super.registerMessage(Language.GERMANY, MessageType.TEAM_YELLOW, "Gelb", () -> null, GommeServerType.values());

        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_BLACK, "Schworz", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_BLUE, "Blau", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_TURQUOISE, "Türkis", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_DIAMOND, "Diamant", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_DARK_BLUE, "D-Blau", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_DARK_GRAY, "D-Grau", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_DARK_GREEN, "D-Grün", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_DARK_RED, "D-Rot", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_GRAY, "Grau", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_GREEN, "Grün", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_ORANGE, "Orange", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_PINK, "Pink", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_PURPLE, "Violett", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_RED, "Rot", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_WHITE, "Weiß", () -> null, GommeServerType.values());
        super.registerMessage(Language.AUSTRIA, MessageType.TEAM_YELLOW, "Gelb", () -> null, GommeServerType.values());
    }

    public MessageType getTeam(Language language, GommeServerType gameMode, String name) {
        Map<MessageType, RegisteredMessage> messages = this.messages.get(language).get(gameMode);
        for (Map.Entry<MessageType, RegisteredMessage> entry : messages.entrySet()) {
            if (!entry.getValue().hasVariables() && entry.getValue().getMessageTester().test(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
