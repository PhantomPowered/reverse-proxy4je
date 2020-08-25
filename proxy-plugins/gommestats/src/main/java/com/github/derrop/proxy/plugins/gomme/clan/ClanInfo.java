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

import com.github.derrop.proxy.plugins.gomme.player.Tag;

import java.util.Collection;

public class ClanInfo {

    private final String name;
    private final String shortcut;
    private final Collection<Tag> tags;
    private final Collection<ClanMember> members;
    private final int unknownMemberCount;
    private final int maxMemberCount;
    private final long timestamp;

    public ClanInfo(String name, String shortcut, Collection<Tag> tags, Collection<ClanMember> members, int unknownMemberCount, int maxMemberCount, long timestamp) {
        this.name = name;
        this.shortcut = shortcut;
        this.tags = tags;
        this.members = members;
        this.unknownMemberCount = unknownMemberCount;
        this.maxMemberCount = maxMemberCount;
        this.timestamp = timestamp;
    }

    public String getName() {
        return this.name;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public Collection<Tag> getTags() {
        return this.tags;
    }

    public Collection<ClanMember> getMembers() {
        return this.members;
    }

    public int getUnknownMemberCount() {
        return this.unknownMemberCount;
    }

    public int getMaxMemberCount() {
        return this.maxMemberCount;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "ClanInfo{"
                + "name='" + name + '\''
                + ", shortcut='" + shortcut + '\''
                + ", tags=" + tags
                + ", members=" + members
                + ", unknownMemberCount=" + unknownMemberCount
                + ", maxMemberCount=" + maxMemberCount
                + ", timestamp=" + timestamp
                + '}';
    }
}
