package com.github.derrop.proxy.plugins.gomme.match.messages;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;

public enum MessageType {

    // Every game mode
    GAME_BEGIN(),
    MAP_SELECTED(),
    GAME_END_FINISHED(),
    GAME_END_DIED(),
    PLAYER_JOIN_LOBBY(),
    PLAYER_LEAVE_LOBBY(),
    PLAYER_LEAVE_IN_GAME(),
    PLAYER_DIED(),
    PLAYER_KILLED(),
    PLAYER_GLOBAL_CHAT_MESSAGE(), // TODO
    PLAYER_TEAM_CHAT_MESSAGE(), // TODO


    // Cores
    CORE_DESTROYED(GommeGameMode.CORES),
    CORE_NEARBY_PLAYERS(GommeGameMode.CORES),


    // BedWars
    BED_DESTROYED(GommeGameMode.BED_WARS),
    CANNOT_DESTROY_OWN_BED(GommeGameMode.BED_WARS),
    TEAM_OUT(GommeGameMode.BED_WARS);

    private final GommeGameMode[] availableGameModes;

    MessageType() {
        this(GommeGameMode.values());
    }

    MessageType(GommeGameMode... availableGameModes) {
        this.availableGameModes = availableGameModes;
    }

    public GommeGameMode[] getAvailableGameModes() {
        return this.availableGameModes;
    }
}
