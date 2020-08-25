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

    private final String registeredName;
    private final Set<String> membershipSet = Sets.newHashSet();
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    private String teamName;
    private String namePrefix = "";
    private String colorSuffix = "";

    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisible = true;

    private Visibility nameTagVisibility = Visibility.ALWAYS;
    private Visibility deathMessageVisibility = Visibility.ALWAYS;

    private int chatFormat = -1;

    public ScorePlayerTeam(String name) {
        this.registeredName = name;
        this.teamName = name;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public String getRegisteredName() {
        return this.registeredName;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.teamName = name;
        }
    }

    @Override
    public Set<String> getMembershipCollection() {
        return this.membershipSet;
    }

    public String getColorPrefix() {
        return this.namePrefix;
    }

    public void setNamePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        } else {
            this.namePrefix = prefix;
        }
    }

    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String suffix) {
        this.colorSuffix = suffix;
    }

    @Override
    public String formatString(String input) {
        return this.getColorPrefix() + input + this.getColorSuffix();
    }

    public static String formatPlayerName(Team team, String playerName) {
        return team == null ? playerName : team.formatString(playerName);
    }

    @Override
    public boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean friendlyFire) {
        this.allowFriendlyFire = friendlyFire;
    }

    @Override
    public boolean getSeeFriendlyInvisiblesEnabled() {
        return this.canSeeFriendlyInvisible;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
        this.canSeeFriendlyInvisible = friendlyInvisibles;
    }

    @Override
    public Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Visibility visibility) {
        this.nameTagVisibility = visibility;
    }

    public void setDeathMessageVisibility(Visibility visibility) {
        this.deathMessageVisibility = visibility;
    }

    public int write() {
        int i = 0;

        if (this.getAllowFriendlyFire()) {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled()) {
            i |= 2;
        }

        return i;
    }

    public void read(int data) {
        this.setAllowFriendlyFire((data & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((data & 2) > 0);
    }

    public void setChatFormat(int chatFormat) {
        this.chatFormat = chatFormat;
    }

    public int getChatFormat() {
        return this.chatFormat;
    }
}
