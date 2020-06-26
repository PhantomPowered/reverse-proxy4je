package com.github.derrop.proxy.plugins.gomme.clan;

import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.github.derrop.proxy.plugins.gomme.messages.MessageRegistry;
import com.github.derrop.proxy.plugins.gomme.messages.MessageType;
import com.google.common.collect.ImmutableMap;

// TODO not tested
public class ClanMessageRegistry extends MessageRegistry {

    public ClanMessageRegistry() {
        super();

        super.registerMessage(Language.GERMANY, MessageType.CLAN_INFO_BEGIN, "§7[§cClans§7] §6 Clan-Informationen ", () -> null, GommeServerType.LOBBY);

        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_NAME,
                "§e Name§7: §6(.*)", (s, matcher) -> ImmutableMap.of("name", matcher.group(1)),
                map -> null,
                GommeServerType.LOBBY
        );
        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_TAG,
                "§e Tag§7: §6(.*)", (s, matcher) -> ImmutableMap.of("tag", matcher.group(1)),
                map -> null,
                GommeServerType.LOBBY
        );

        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_GENERAL_MEMBER_COUNT,
                "§e Mitglieder§7: (§c|)(\\d+)(§7|)/(\\d+)", (s, matcher) -> ImmutableMap.of("count", matcher.group(2), "max", matcher.group(4)),
                map -> null,
                GommeServerType.LOBBY
        );

        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_LEADER_COUNT,
                "§e Clanleader §7\\((\\S+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1).replace(".", "")),
                map -> null,
                GommeServerType.LOBBY
        );
        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_MODERATOR_COUNT,
                "§e Clanmoderatoren §7\\((\\S+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1).replace(".", "")),
                map -> null,
                GommeServerType.LOBBY
        );
        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_MEMBER_COUNT,
                "§e Clanmitglieder §7\\((\\S+)\\):", (s, matcher) -> ImmutableMap.of("count", matcher.group(1).replace(".", "")),
                map -> null,
                GommeServerType.LOBBY
        );

        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_USER,
                "§7- §(.)(.*)", (s, matcher) -> ImmutableMap.of("rankColor", matcher.group(1), "name", matcher.group(2)),
                map -> null,
                GommeServerType.LOBBY
        );

        super.registerRegExMessage(Language.GERMANY, MessageType.CLAN_INFO_MORE,
                "§7und §e(\\S+) §7weitere", (s, matcher) -> ImmutableMap.of("count", matcher.group(1).replace(".", "")),
                map -> null,
                GommeServerType.LOBBY
        );
    }

}
