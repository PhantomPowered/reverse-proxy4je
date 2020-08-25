package com.github.derrop.proxy.plugins.gommecw.running;

import com.github.derrop.proxy.api.location.Location;
import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class ClanWarTeam {

    @SerializedName("team")
    private final Color color;
    @SerializedName("bed_alive")
    private final boolean bedAlive;
    @SerializedName("clan-name")
    private final String clanName;
    @SerializedName("clan-tag")
    private final String clanTag;
    private final Collection<ClanWarMember> members;

    private transient Location bedLocation;

    public ClanWarTeam(Color color, boolean bedAlive, String clanName, String clanTag, Collection<ClanWarMember> members) {
        this.color = color;
        this.bedAlive = bedAlive;
        this.clanName = clanName;
        this.clanTag = clanTag;
        this.members = members;
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public Color getColor() {
        return color;
    }

    public boolean isBedAlive() {
        return bedAlive;
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanTag() {
        return clanTag;
    }

    public Collection<ClanWarMember> getMembers() {
        return members;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    @Override
    public String toString() {
        return "ClanWarTeam{"
                + "color=" + color
                + ", bedAlive=" + bedAlive
                + ", clanName='" + clanName + '\''
                + ", clanTag='" + clanTag + '\''
                + ", members=" + members
                + ", bedLocation=" + bedLocation
                + '}';
    }

    public enum Color {
        RED(java.awt.Color.RED/*new Color(16733525) MC §c */),
        BLUE(java.awt.Color.BLUE/*new Color(5592575) MC §9 */);

        private final java.awt.Color color;

        Color(java.awt.Color color) {
            this.color = color;
        }

        public java.awt.Color getColor() {
            return this.color;
        }
    }

}
