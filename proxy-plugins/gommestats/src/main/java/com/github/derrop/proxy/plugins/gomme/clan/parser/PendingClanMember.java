package com.github.derrop.proxy.plugins.gomme.clan.parser;

import com.github.derrop.proxy.api.util.player.PlayerIdRepository;
import com.github.derrop.proxy.plugins.gomme.clan.ClanMember;

public class PendingClanMember {

    private final String name;
    private final ClanMember.Type type;
    private final ClanMember.Rank rank;

    public PendingClanMember(String name, ClanMember.Type type, ClanMember.Rank rank) {
        this.name = name;
        this.type = type;
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public ClanMember.Type getType() {
        return this.type;
    }

    public ClanMember.Rank getRank() {
        return this.rank;
    }

    public ClanMember toMember(PlayerIdRepository repository) {
        return new ClanMember(repository.getPlayerId(this.name), this.type, this.rank);
    }

}
