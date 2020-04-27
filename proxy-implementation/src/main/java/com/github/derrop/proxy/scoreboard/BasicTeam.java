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
package com.github.derrop.proxy.scoreboard;

import com.github.derrop.proxy.api.scoreboard.NameTagVisibility;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.scoreboard.minecraft.ScorePlayerTeam;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardTeam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicTeam implements Team {

    private final BasicScoreboard scoreboard;
    private final String name;
    private ScorePlayerTeam handle;

    public BasicTeam(BasicScoreboard scoreboard, String name, ScorePlayerTeam handle) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.handle = handle;
    }

    private void ensureRegistered() {
        if (this.scoreboard.getHandle().getTeam(this.name) == null) {
            ScorePlayerTeam team = this.scoreboard.getHandle().createTeam(this.name);
            if (this.handle != null) {
                team.setNamePrefix(this.handle.getColorPrefix());
                team.setNameSuffix(this.handle.getColorSuffix());
                team.setChatFormat(this.handle.getChatFormat());
                team.setAllowFriendlyFire(this.handle.getAllowFriendlyFire());
                team.setNameTagVisibility(this.handle.getNameTagVisibility());
                team.setDeathMessageVisibility(this.handle.getDeathMessageVisibility());
            }

            this.handle = team;
            this.scoreboard.getCache().sendTeamCreation(team);
        }
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public Set<String> getEntries() {
        this.ensureRegistered();
        return Collections.unmodifiableSet(this.handle.getMembershipCollection());
    }

    @Override
    public boolean hasEntry(String entry) {
        this.ensureRegistered();
        return this.handle.getMembershipCollection().contains(entry);
    }

    @Override
    public void addEntry(String entry) {
        this.ensureRegistered();
        if (this.handle.getMembershipCollection().contains(entry)) {
            return;
        }
        this.scoreboard.getHandle().addPlayerToTeam(entry, this.name);
        this.scoreboard.getCache().sendTeamUpdate(new PacketPlayServerScoreboardTeam(
                this.name, (byte) 3, null, null, null, null, null, 0, (byte) 0,
                new String[]{entry}
        ));
    }

    @Override
    public void removeEntry(String entry) {
        this.ensureRegistered();
        if (!this.handle.getMembershipCollection().contains(entry)) {
            return;
        }
        this.scoreboard.getHandle().removePlayerFromTeam(entry, this.handle);
        this.scoreboard.getCache().sendTeamUpdate(new PacketPlayServerScoreboardTeam(
                this.name, (byte) 4, null, null, null, null, null, 0, (byte) 0,
                new String[]{entry}
        ));
    }

    @Override
    public String getDisplayName() {
        this.ensureRegistered();
        return this.handle.getTeamName();
    }

    @Override
    public void setDisplayName(String displayName) {
        this.ensureRegistered();
        this.handle.setTeamName(displayName);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NameTagVisibility getNameTagVisibility() {
        this.ensureRegistered();
        return NameTagVisibility.values()[this.handle.getNameTagVisibility().field_178827_f];
    }

    @Override
    public void unregister() {
        if (this.handle != null) {
            this.scoreboard.getHandle().removeTeam(this.handle);
            this.scoreboard.getCache().sendTeamUpdate(new PacketPlayServerScoreboardTeam(this.name));
        }
    }

    @Override
    public boolean allowFriendlyFire() {
        this.ensureRegistered();
        return this.handle.getAllowFriendlyFire();
    }

    @Override
    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        this.ensureRegistered();
        if (this.handle.getAllowFriendlyFire() == allowFriendlyFire) {
            return;
        }
        this.handle.setAllowFriendlyFire(allowFriendlyFire);
        this.scoreboard.getCache().sendTeamUpdate((byte) 2, this.handle);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        this.ensureRegistered();
        return this.handle.getSeeFriendlyInvisiblesEnabled();
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.ensureRegistered();
        if (this.handle.getSeeFriendlyInvisiblesEnabled() == canSeeFriendlyInvisibles) {
            return;
        }
        this.handle.setSeeFriendlyInvisiblesEnabled(canSeeFriendlyInvisibles);
        this.scoreboard.getCache().sendTeamUpdate((byte) 2, this.handle);
    }

    @Override
    public String getPrefix() {
        this.ensureRegistered();
        return this.handle.getColorPrefix();
    }

    @Override
    public void setPrefix(String prefix) {
        this.ensureRegistered();
        if (this.handle.getColorPrefix().equals(prefix)) {
            return;
        }
        this.handle.setNamePrefix(prefix);
        this.scoreboard.getCache().sendTeamUpdate((byte) 2, this.handle);
    }

    @Override
    public String getSuffix() {
        this.ensureRegistered();
        return this.handle.getColorSuffix();
    }

    @Override
    public void setSuffix(String suffix) {
        this.ensureRegistered();
        if (this.handle.getColorSuffix().equals(suffix)) {
            return;
        }
        this.handle.setNameSuffix(suffix);
        this.scoreboard.getCache().sendTeamUpdate((byte) 2, this.handle);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.handle == null ? new HashMap<>() : this.handle.getProperties();
    }
}
