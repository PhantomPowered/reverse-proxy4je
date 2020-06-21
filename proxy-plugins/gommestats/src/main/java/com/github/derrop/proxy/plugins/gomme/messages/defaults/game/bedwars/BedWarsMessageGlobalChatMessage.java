package com.github.derrop.proxy.plugins.gomme.messages.defaults.game.bedwars;

import com.github.derrop.proxy.plugins.gomme.match.event.global.player.PlayerChatEvent;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SingleGameMessageRegistrar;
import com.github.derrop.proxy.plugins.gomme.messages.defaults.game.SpecificGameMessageRegistrar;
import com.google.common.collect.ImmutableMap;

public class BedWarsMessageGlobalChatMessage extends SingleGameMessageRegistrar {
    @Override
    public void register(SpecificGameMessageRegistrar registrar) {
        registrar.registerRegExMessage(Language.GERMANY, MessageType.PLAYER_GLOBAL_CHAT_MESSAGE, "^(?!\\[BedWars\\])((?:\\S+ )?(\\S+): (.*))",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(2), "message", matcher.group(3)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), false)
        );
        registrar.registerRegExMessage(Language.AUSTRIA, MessageType.PLAYER_GLOBAL_CHAT_MESSAGE, "^(?!\\[BedWars\\])((?:\\S+ )?(\\S+): (.*))", // TODO not tested
                (input, matcher) -> ImmutableMap.of("player", matcher.group(2), "message", matcher.group(3)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), false)
        );
    }
}
