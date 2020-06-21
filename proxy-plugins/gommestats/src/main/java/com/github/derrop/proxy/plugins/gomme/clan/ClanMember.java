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
package com.github.derrop.proxy.plugins.gomme.clan;

import com.github.derrop.proxy.api.util.player.PlayerId;

import java.util.Arrays;

public class ClanMember {

    private PlayerId playerId;
    private Type memberType;
    private Rank rank;

    public ClanMember(PlayerId playerId, Type memberType, Rank rank) {
        this.playerId = playerId;
        this.memberType = memberType;
        this.rank = rank;
    }

    public PlayerId getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(PlayerId playerId) {
        this.playerId = playerId;
    }

    public Type getMemberType() {
        return memberType;
    }

    public void setMemberType(Type memberType) {
        this.memberType = memberType;
    }

    public Rank getRank() {
        return this.rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "ClanMember{" +
                "playerId=" + playerId +
                ", memberType=" + memberType +
                ", rank=" + rank +
                '}';
    }

    public static enum Type {
        LEADER, MODERATOR, MEMBER
    }

    public static enum Rank {
        ADMIN("4"),
        TEAM("c"),
        YOUTUBER("5"),
        SUPREMIUM("b"),
        PREMIUM_PLUS("e"),
        PREMIUM("6"),
        PLAYER("a"),
        UNKNOWN(null);

        private final String requiredColor;

        Rank(String requiredColor) {
            this.requiredColor = requiredColor;
        }

        public String getRequiredColor() {
            return this.requiredColor;
        }

        public static Rank parseRank(String color) {
            return Arrays.stream(values())
                    .filter(rank -> rank.requiredColor != null && rank.requiredColor.equals(color))
                    .findFirst()
                    .orElse(Rank.UNKNOWN);
        }

    }

}
