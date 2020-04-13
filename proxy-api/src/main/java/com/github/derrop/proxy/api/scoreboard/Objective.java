package com.github.derrop.proxy.api.scoreboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Objective {

    @NotNull
    Scoreboard getScoreboard();

    @NotNull
    String getCriteria();

    @NotNull
    String getName();

    @NotNull
    String getDisplayName();

    @Nullable
    DisplaySlot getDisplaySlot();

    void setDisplayName(@NotNull String displayName);

    void setDisplaySlot(@NotNull DisplaySlot displaySlot);

    void unregister();

    @NotNull
    Score getScore(@NotNull String entry);

    void unregisterScore(@NotNull String entry);

}
