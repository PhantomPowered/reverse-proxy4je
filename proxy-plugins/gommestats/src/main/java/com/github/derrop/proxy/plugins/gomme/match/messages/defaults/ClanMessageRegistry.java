package com.github.derrop.proxy.plugins.gomme.match.messages.defaults;

import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageRegistry;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.google.common.collect.ImmutableMap;

// TODO not tested
public class ClanMessageRegistry extends MessageRegistry {

    public ClanMessageRegistry() {
        super();

        super.registerMessage(Language.GERMAN, MessageType.CLAN_INFO_BEGIN, "[Clans]  Clan-Informationen", () -> null);

        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_NAME,
                " Name: (.*)", (s, matcher) -> ImmutableMap.of("name", matcher.group(1)),
                map -> null
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_TAG,
                " Tag: (.*)", (s, matcher) -> ImmutableMap.of("tag", matcher.group(1)),
                map -> null
        );

        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_GENERAL_MEMBER_COUNT,
                " Mitglieder: (\\d+)/(\\d+)", (s, matcher) -> ImmutableMap.of("count", matcher.group(1), "max", matcher.group(2)),
                map -> null
        );

        super.registerMessage(Language.GERMAN, MessageType.CLAN_INFO_CLAN_PAGE, " Clanpage: Klick", () -> null);

        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_LEADER_COUNT,
                " Clanleader \\((\\d+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1)),
                map -> null
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_MODERATOR_COUNT,
                " Clanmoderatoren \\((\\d+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1)),
                map -> null
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_MEMBER_COUNT,
                " Clanmitglieder \\((\\d+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1)),
                map -> null
        );

        super.registerRegExMessage(Language.GERMAN, MessageType.CLAN_INFO_USER, // TODO stripColor has to be disabled for this to work
                "§7- §(.)(.*)", (s, matcher) -> ImmutableMap.of("rankColor", matcher.group(1), "name", matcher.group(2)),
                map -> null
        );
    }

}
