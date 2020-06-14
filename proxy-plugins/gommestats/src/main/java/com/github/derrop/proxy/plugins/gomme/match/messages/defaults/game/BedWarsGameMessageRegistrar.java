package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedDestroyEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.bedwars.BedWarsTryDestroyOwnBedEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.global.match.*;
import com.github.derrop.proxy.plugins.gomme.match.event.global.player.*;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;
import com.github.derrop.proxy.plugins.gomme.match.messages.defaults.TeamRegistry;
import com.google.common.collect.ImmutableMap;

public class BedWarsGameMessageRegistrar extends SpecificGameMessageRegistrar {
    public BedWarsGameMessageRegistrar(GameMessageRegistry registry, TeamRegistry teamRegistry) {
        super(GommeGameMode.BED_WARS, registry, teamRegistry);
    }

    @Override
    public void init() {
        super.registerMessage(Language.GERMAN, MessageType.GAME_BEGIN, "[BedWars] Das Spiel beginnt!", MatchBeginEvent::new);
        super.registerMessage(Language.GERMAN, MessageType.GAME_END_DIED, "[BedWars] Du bist nun Zuschauer!", MatchEndDiedEvent::new);
        super.registerRegExMessage(Language.GERMAN, MessageType.GAME_END_FINISHED, "\\[BedWars] Team (.*) hat gewonnen!",
                (input, matcher) -> ImmutableMap.of("winner", matcher.group(1)),
                map -> new MatchEndFinishedEvent(super.getTeam(Language.GERMAN, map.get("winner")))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.MAP_SELECTED, "\\[BedWars] Map: (.*) von: (.*)",
                (input, matcher) -> ImmutableMap.of("map", matcher.group(1), "builder", matcher.group(2)),
                map -> new MapSelectedEvent(map.get("map"), map.get("builder"))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_JOIN_LOBBY, "» (.*) hat das Spiel betreten",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerJoinLobbyEvent(map.get("player"))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_LEAVE_LOBBY, "« (.*) hat das Spiel verlassen",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerLeaveLobbyEvent(map.get("player"))
        );
        super.registerMessage(Language.GERMAN, MessageType.CANNOT_DESTROY_OWN_BED, "[BedWars] Du kannst dein eigenes Bett nicht zerstören!", BedWarsTryDestroyOwnBedEvent::new);
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_DIED,
                "\\[BedWars] (.*) ist gestorben",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1)),
                map -> new PlayerDiedEvent(map.get("player"))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_KILLED,
                "\\[BedWars] (.*) wurde von (.*) getötet",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "killer", matcher.group(2)),
                map -> new PlayerKilledEvent(map.get("player"), map.get("killer"))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_LEAVE_IN_GAME,
                "\\[BedWars] (.*) hat das Spiel verlassen. Team (.*) hat noch (\\d+) Spieler",
                (input, matcher) ->
                        ImmutableMap.of("player", matcher.group(1), "team", matcher.group(2), "remaining", matcher.group(3)),
                map -> new PlayerLeaveInGameEvent(map.get("player"), super.getTeam(Language.GERMAN, map.get("team")), Integer.parseInt(map.get("remaining")))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.BED_DESTROYED,
                "\\[BedWars] Das Bett von Team (.*) wurde von (.*) zerstört!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1), "destroyer", matcher.group(2)),
                map -> new BedDestroyEvent(map.get("destroyer"), super.getTeam(Language.GERMAN, map.get("team")))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.TEAM_OUT, "\\[BedWars] Team (.*) wurde vernichtet!",
                (input, matcher) -> ImmutableMap.of("team", matcher.group(1)),
                map -> new TeamOutEvent(super.getTeam(Language.GERMAN, map.get("team")))
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_TEAM_CHAT_MESSAGE, "\\[@all\\] (?:\\S+ )?(\\S+): (.*)",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(1), "message", matcher.group(2)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), true)
        );
        super.registerRegExMessage(Language.GERMAN, MessageType.PLAYER_GLOBAL_CHAT_MESSAGE, "^(?!\\[BedWars\\])((?:\\S+ )?(\\S+): (.*))",
                (input, matcher) -> ImmutableMap.of("player", matcher.group(2), "message", matcher.group(3)),
                map -> new PlayerChatEvent(map.get("player"), map.get("message"), false)
        );
    }
}
