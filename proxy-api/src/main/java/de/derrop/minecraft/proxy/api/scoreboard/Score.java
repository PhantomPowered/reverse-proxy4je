package de.derrop.minecraft.proxy.api.scoreboard;

import org.jetbrains.annotations.NotNull;

public interface Score {

    @NotNull
    String getEntry();

    @NotNull
    Objective getObjective();

    int getScore();

    void setScore(int score);

}
