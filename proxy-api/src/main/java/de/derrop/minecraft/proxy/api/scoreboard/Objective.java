package de.derrop.minecraft.proxy.api.scoreboard;

import org.jetbrains.annotations.NotNull;

public interface Objective {

    @NotNull
    Scoreboard getScoreboard();

    @NotNull
    String getCriteria();

    @NotNull
    String getDisplayName();

    @NotNull
    DisplaySlot getDisplaySlot();

    void setDisplayName(@NotNull String displayName);

    void setDisplaySlot(@NotNull DisplaySlot displaySlot);

    void unregister();

    @NotNull
    Score getScore(@NotNull String entry);

}
