/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.connection.player.scoreboard.minecraft;

import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScorePlayerTeam extends Team {

    private final Scoreboard theScoreboard;
    private final String registeredName;
    private final Set<String> membershipSet = Sets.<String>newHashSet();
    private String teamNameSPT;
    private String namePrefixSPT = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private EnumVisible nameTagVisibility = EnumVisible.ALWAYS;
    private EnumVisible deathMessageVisibility = EnumVisible.ALWAYS;
    private int chatFormat = -1;

    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    public ScorePlayerTeam(Scoreboard theScoreboardIn, String name) {
        this.theScoreboard = theScoreboardIn;
        this.registeredName = name;
        this.teamNameSPT = name;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard.
     */
    @Override
    public String getRegisteredName() {
        return this.registeredName;
    }

    public String getTeamName() {
        return this.teamNameSPT;
    }

    public void setTeamName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.teamNameSPT = name;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    @Override
    public Set<String> getMembershipCollection() {
        return this.membershipSet;
    }

    /**
     * Returns the color prefix for the player's team name.
     */
    public String getColorPrefix() {
        return this.namePrefixSPT;
    }

    public void setNamePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        } else {
            this.namePrefixSPT = prefix;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    /**
     * Returns the color suffix for the player's team name.
     */
    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String suffix) {
        this.colorSuffix = suffix;
        this.theScoreboard.sendTeamUpdate(this);
    }

    @Override
    public String formatString(String input) {
        return this.getColorPrefix() + input + this.getColorSuffix();
    }

    /**
     * Returns the player name including the color prefixes and suffixes.
     */
    public static String formatPlayerName(Team p_96667_0_, String p_96667_1_) {
        return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.formatString(p_96667_1_);
    }

    @Override
    public boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean friendlyFire) {
        this.allowFriendlyFire = friendlyFire;
        this.theScoreboard.sendTeamUpdate(this);
    }

    @Override
    public boolean getSeeFriendlyInvisiblesEnabled() {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
        this.canSeeFriendlyInvisibles = friendlyInvisibles;
        this.theScoreboard.sendTeamUpdate(this);
    }

    @Override
    public EnumVisible getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public EnumVisible getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(EnumVisible p_178772_1_) {
        this.nameTagVisibility = p_178772_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public void setDeathMessageVisibility(EnumVisible p_178773_1_) {
        this.deathMessageVisibility = p_178773_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public int func_98299_i() {
        int i = 0;

        if (this.getAllowFriendlyFire()) {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled()) {
            i |= 2;
        }

        return i;
    }

    public void func_98298_a(int p_98298_1_) {
        this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
    }

    public void setChatFormat(int p_178774_1_) {
        this.chatFormat = p_178774_1_;
    }

    public int getChatFormat() {
        return this.chatFormat;
    }
}
