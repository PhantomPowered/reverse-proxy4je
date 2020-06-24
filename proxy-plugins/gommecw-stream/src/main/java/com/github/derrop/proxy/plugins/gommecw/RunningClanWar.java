package com.github.derrop.proxy.plugins.gommecw;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyConnection;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserData;
import com.google.common.base.Preconditions;
import com.mojang.authlib.UserAuthentication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RunningClanWar {

    private final Collection<PlayerInfo> spectators = new CopyOnWriteArrayList<>();
    private final MatchInfo matchInfo;
    private Collection<UUID> labyUsers;

    public RunningClanWar(MatchInfo matchInfo) {
        this.matchInfo = matchInfo;
    }

    private void loadLabyUsers(ServiceRegistry registry, UserAuthentication authentication) throws InterruptedException {
        Preconditions.checkArgument(authentication.isLoggedIn(), "tried to load laby users with an authentication that is not logged in");
        LabyConnection labyConnection = new LabyConnection(registry, authentication);
        labyConnection.connect();

        Thread.sleep(1000);

        UUID[] request = this.matchInfo.getPlayers().stream().map(PlayerInfo::getUniqueId).toArray(UUID[]::new);
        UserData[] data = labyConnection.requestUserData(request).getUninterruptedly(10, TimeUnit.SECONDS);

        this.labyUsers = data == null ? Collections.emptyList() : Collections.unmodifiableCollection(Arrays.stream(data).map(UserData::getUniqueId).collect(Collectors.toList()));

        labyConnection.close();
    }

    public MatchInfo getMatchInfo() {
        return this.matchInfo;
    }

    public Collection<UUID> getLabyUsers() {
        return this.labyUsers;
    }

    public boolean isLabyModUser(UUID uniqueId) {
        return this.labyUsers.contains(uniqueId);
    }

    public boolean isBACUser(UUID uniqueId) {
        return this.matchInfo.getBacPlayers().contains(uniqueId);
    }

}
