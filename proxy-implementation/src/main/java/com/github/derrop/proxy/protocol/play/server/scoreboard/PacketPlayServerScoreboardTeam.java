package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardTeam implements Packet {

    private String name;
    private byte mode;
    private String displayName;
    private String prefix;
    private String suffix;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private byte friendlyFire;
    private String[] players;

    public PacketPlayServerScoreboardTeam(String name) {
        this.name = name;
        this.mode = 1;
    }

    public PacketPlayServerScoreboardTeam(String name, byte mode, String displayName, String prefix, String suffix, String nameTagVisibility, String collisionRule, int color, byte friendlyFire, String[] players) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nameTagVisibility = nameTagVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.friendlyFire = friendlyFire;
        this.players = players;
    }

    public PacketPlayServerScoreboardTeam() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_TEAM;
    }

    public String getName() {
        return this.name;
    }

    public byte getMode() {
        return this.mode;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }

    public int getColor() {
        return this.color;
    }

    public byte getFriendlyFire() {
        return this.friendlyFire;
    }

    public String[] getPlayers() {
        return this.players;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.name = protoBuf.readString();
        this.mode = protoBuf.readByte();

        if (this.mode == 0 || this.mode == 2) {
            this.displayName = protoBuf.readString();
            this.prefix = protoBuf.readString();
            this.suffix = protoBuf.readString();
            this.friendlyFire = protoBuf.readByte();
            this.nameTagVisibility = protoBuf.readString();
            this.color = protoBuf.readByte();
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            int length = protoBuf.readVarInt();
            this.players = new String[length];

            for (int i = 0; i < length; i++) {
                this.players[i] = protoBuf.readString();
            }
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.name);
        protoBuf.writeByte(this.mode);

        if (this.mode == 0 || this.mode == 2) {
            protoBuf.writeString(this.displayName);
            protoBuf.writeString(this.prefix);
            protoBuf.writeString(this.suffix);
            protoBuf.writeByte(this.friendlyFire);
            protoBuf.writeString(this.nameTagVisibility);
            protoBuf.writeByte(this.color);
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            protoBuf.writeVarInt(this.players.length);
            for (String player : this.players) {
                protoBuf.writeString(player);
            }
        }
    }

    public String toString() {
        return "PacketPlayServerScoreboardTeam(name=" + this.getName() + ", mode=" + this.getMode() + ", displayName=" + this.getDisplayName() + ", prefix=" + this.getPrefix() + ", suffix=" + this.getSuffix() + ", nameTagVisibility=" + this.getNameTagVisibility() + ", collisionRule=" + this.getCollisionRule() + ", color=" + this.getColor() + ", friendlyFire=" + this.getFriendlyFire() + ", players=" + java.util.Arrays.deepToString(this.getPlayers()) + ")";
    }
}
