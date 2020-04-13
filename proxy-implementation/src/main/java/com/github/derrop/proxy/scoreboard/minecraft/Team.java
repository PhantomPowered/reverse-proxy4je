package com.github.derrop.proxy.scoreboard.minecraft;

import java.util.Collection;

public abstract class Team {

    /**
     * Same as ==
     */
    public boolean isSameTeam(Team other) {
        return other == null ? false : this == other;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public abstract String getRegisteredName();

    public abstract String formatString(String input);

    public abstract boolean getSeeFriendlyInvisiblesEnabled();

    public abstract boolean getAllowFriendlyFire();

    public abstract EnumVisible getNameTagVisibility();

    public abstract Collection<String> getMembershipCollection();

    public abstract EnumVisible getDeathMessageVisibility();
}
