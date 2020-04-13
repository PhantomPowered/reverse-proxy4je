package com.github.derrop.proxy.api.scoreboard;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Scoreboard {

    ServiceConnection getAssociatedConnection();


    void clearSlot(@NotNull DisplaySlot displaySlot);

    @Nullable
    Objective getObjective(@NotNull String name);

    @Nullable
    Objective getObjective(@NotNull DisplaySlot displaySlot);

    @NotNull
    Objective registerNewObjective(@NotNull String name);


    @NotNull
    Set<Team> getTeams();

    @Nullable
    Team getTeam(@NotNull String name);

    @NotNull
    Team registerNewTeam(@NotNull String name);

}
