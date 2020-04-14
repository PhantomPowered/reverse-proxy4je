package com.github.derrop.proxy.api.scoreboard;

import java.util.Set;

public interface Team {

    Scoreboard getScoreboard();

    Set<String> getEntries();

    boolean hasEntry(String entry);

    void addEntry(String entry);

    void removeEntry(String entry);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getName();

    NameTagVisibility getNameTagVisibility();

    void unregister();

    boolean allowFriendlyFire();

    void setAllowFriendlyFire(boolean allowFriendlyFire);

    boolean canSeeFriendlyInvisibles();

    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);


    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

}
