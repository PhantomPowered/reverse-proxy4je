package com.github.derrop.proxy.plugins.gomme.messages;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardScoreSetEvent;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;

public class LanguageDetector extends MessageRegistry {

    private final MatchManager matchManager;

    public LanguageDetector(MatchManager matchManager) {
        super();
        this.matchManager = matchManager;

        this.registerRankMessage(Language.GERMANY, "Dein Rang:");
        this.registerRankMessage(Language.US, "Your rank:");
        this.registerRankMessage(Language.FRANCE, "Ton rang :");
        this.registerRankMessage(Language.RUSSIA, "Твой ранг:");
        this.registerRankMessage(Language.TURKISH, "Senin Rütben:");
        this.registerRankMessage(Language.NETHERLANDS, "Jouw rang:");
        this.registerRankMessage(Language.POLISH, "Twoja ranga:");
        this.registerRankMessage(Language.NORWAY, "Din rang:");
        this.registerRankMessage(Language.AUSTRIA, "Dei Rong:");
        this.registerRankMessage(Language.SPANISH, "Tu rango:");
        this.registerRankMessage(Language.PORTUGAL, "A tua posição:");
        this.registerRankMessage(Language.BRAZIL, "Tempo de jogo:");
        this.registerRankMessage(Language.CZECH, "Tvůj rank:");
        this.registerRankMessage(Language.SWEDISH, "Din rank:");
        this.registerRankMessage(Language.HUNGARIAN, "Rangod:");
        this.registerRankMessage(Language.JAPANESE, "自分のランク：");
        this.registerRankMessage(Language.CROATIAN, "Tvoj rank:");
        this.registerRankMessage(Language.SWISS, "Din Rang:");
        this.registerRankMessage(Language.LUXEMBOURG, "Däin Rang:");

        this.registerSelectMessage(Language.GERMANY, "Deine Sprache ist nun Deutsch (Deutschland)");
        this.registerSelectMessage(Language.US, "Your language is now English (United States)");
        this.registerSelectMessage(Language.FRANCE, "Ta langue est maintenant français (France)");
        this.registerSelectMessage(Language.RUSSIA, "Твой язык сейчас русский (Россия)");
        this.registerSelectMessage(Language.TURKISH, "Diliniz artık Türkçe (Türkiye)");
        this.registerSelectMessage(Language.NETHERLANDS, "Uw taal is nu Nederlands (Nederland)");
        this.registerSelectMessage(Language.POLISH, "Twój język to polski (Polska)");
        this.registerSelectMessage(Language.NORWAY, "Språket ditt er nå norsk (Norge)");
        this.registerSelectMessage(Language.AUSTRIA, "Dei Sproch is jetz Deutsch (Österreich)");
        this.registerSelectMessage(Language.SPANISH, "Se ha ajustado el idioma a español (España)");
        this.registerSelectMessage(Language.PORTUGAL, "Your language is now português (Portugal)");
        this.registerSelectMessage(Language.BRAZIL, "Your language is now português (Brasil)");
        this.registerSelectMessage(Language.CZECH, "Změnil sis jazyk na čeština (Česká republika)");
        this.registerSelectMessage(Language.SWEDISH, "Ditt språk är nu svenska (Sverige)");
        this.registerSelectMessage(Language.HUNGARIAN, "Your language is now magyar (Magyarország)");
        this.registerSelectMessage(Language.JAPANESE, "言語が 日本語 (日本) に設定されました");
        this.registerSelectMessage(Language.CROATIAN, "Váš jazyk je teraz Slovenčina (Slovenská republika)");
        this.registerSelectMessage(Language.SWISS, "Dini Sprach isch jetzt Deutsch (Schweiz)");
        this.registerSelectMessage(Language.LUXEMBOURG, "Deng Sprooch ass elo Luxembourgish (Luxembourg)");
    }

    private void registerRankMessage(Language language, String message) {
        super.registerMessage(language, MessageType.YOUR_RANK_INFO, message, () -> null, GommeServerType.LOBBY);
    }

    private void registerSelectMessage(Language language, String message) {
        super.registerMessage(language, MessageType.LANGUAGE_SELECTED, message, () -> null, GommeServerType.values());
    }

    @Listener
    public void handleScoreboard(ScoreboardScoreSetEvent event) {
        if (event.getScore().getScore() != 10) {
            return;
        }

        ServiceConnection connection = event.getConnection();

        GommeServerType serverType = event.getConnection().getProperty(GommeConstants.CURRENT_SERVER_PROPERTY);
        if (serverType != GommeServerType.LOBBY) {
            return;
        }

        String input = event.getScore().getEntry();

        Language language = this.getLanguage(serverType, MessageType.YOUR_RANK_INFO, input);
        if (language == null) {
            return;
        }

        this.updateLanguage(connection, language);
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || event.getType() != ChatMessageType.SYSTEM) {
            return;
        }
        ServiceConnection connection = (ServiceConnection) event.getConnection();

        GommeServerType serverType = event.getConnection().getProperty(GommeConstants.CURRENT_SERVER_PROPERTY);
        if (serverType == null || serverType == GommeServerType.LOBBY) {
            return;
        }

        String input = LegacyComponentSerializer.legacySection().serialize(event.getMessage());
        int breakIndex = input.indexOf('\n');
        if (breakIndex == -1) {
            return;
        }
        input = input.substring(0, breakIndex);

        Language language = this.getLanguage(serverType, MessageType.LANGUAGE_SELECTED, input);

        if (language == null) {
            return;
        }

        this.updateLanguage(connection, language);
    }

    private void updateLanguage(ServiceConnection connection, Language language) {
        if (connection.getProperty(GommeConstants.SELECTED_LANGUAGE) == language) {
            return;
        }

        MatchInfo matchInfo = this.matchManager.getMatch(connection);
        if (matchInfo != null) {
            matchInfo.setSelectedLanguage(language);
        }

        connection.setProperty(GommeConstants.SELECTED_LANGUAGE, language);
    }

    private Language getLanguage(GommeServerType serverType, MessageType type, String input) {
        input = ChatColor.stripColor(input);

        for (Map.Entry<Language, Map<GommeServerType, Map<MessageType, RegisteredMessage>>> entry : super.getMessages().entrySet()) {
            Map<MessageType, RegisteredMessage> messages = entry.getValue().get(serverType);
            RegisteredMessage message = messages.get(type);
            if (message == null) {
                continue;
            }

            if (message.getMessageTester().test(input)) {
                return entry.getKey();
            }
        }

        return null;
    }

}
