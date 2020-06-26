package com.github.derrop.proxy.plugins.gomme.clan.parser;

import com.github.derrop.proxy.api.util.player.PlayerIdRepository;
import com.github.derrop.proxy.plugins.gomme.clan.ClanInfo;
import com.github.derrop.proxy.plugins.gomme.clan.ClanMember;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

public class PendingClanRequest {

    private String name;
    private String shortcut;
    private int generalMemberCount;
    private int maxMemberCount;
    private final Map<ClanMember.Type, Integer> memberCount = new HashMap<>();
    private final Collection<PendingClanMember> members = new ArrayList<>();
    private ClanMember.Type readingMemberType;
    private int unknownMemberCount = -1;

    public ClanMember.Type getReadingMemberType() {
        return this.readingMemberType;
    }

    public void setMemberCount(ClanMember.Type type, int count) {
        this.readingMemberType = type;
        this.memberCount.put(type, count);
    }

    public boolean hasEnoughMembers() {
        return Arrays.stream(ClanMember.Type.values()).allMatch(this::hasEnoughMembers);
    }

    public boolean hasEnoughMembers(ClanMember.Type type) {
        int count = this.memberCount.getOrDefault(type, -1);
        return this.members.stream().filter(member -> member.getType() == type).count() == count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public void setGeneralMemberCount(int generalMemberCount) {
        this.generalMemberCount = generalMemberCount;
    }

    public void setUnknownMemberCount(int unknownMemberCount) {
        this.unknownMemberCount = unknownMemberCount;
    }

    public void setMaxMemberCount(int maxMemberCount) {
        this.maxMemberCount = maxMemberCount;
    }

    public void addMember(PendingClanMember member) {
        this.members.add(member);
    }

    public ClanInfo toClanInfo(PlayerIdRepository repository) {
        Preconditions.checkNotNull(this.name, "name");
        Preconditions.checkNotNull(this.shortcut, "shortcut");
        Preconditions.checkArgument(this.generalMemberCount == this.members.size() || this.unknownMemberCount != -1, "Wrong member count");

        Collection<ClanMember> members = this.members.stream()
                .map(member -> member.toMember(repository))
                .collect(Collectors.toList());

        return new ClanInfo(this.name, this.shortcut, new ArrayList<>(), members, this.unknownMemberCount, this.maxMemberCount, System.currentTimeMillis());
    }

}
