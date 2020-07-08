package com.github.derrop.proxy.plugins.gommecw.running;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gommecw.image.Frame;
import com.mojang.authlib.UserAuthentication;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RunningClanWar {

    private final Collection<PlayerInfo> spectators = new CopyOnWriteArrayList<>(); // TODO fill
    private final Collection<ClanWarTeam> teams = new ArrayList<>();
    private final RunningClanWarInfo info;
    private final MatchInfo matchInfo;
    private final Collection<ServiceConnection> ourSpectators = new ArrayList<>();
    private Collection<UUID> labyUsers;
    private Frame frame;
    private final transient Collection<UUID> blockStateTrackerIds = new CopyOnWriteArrayList<>();
    private final transient Map<String, Object> properties = new ConcurrentHashMap<>();

    public RunningClanWar(RunningClanWarInfo info, MatchInfo matchInfo) {
        this.info = info;
        this.matchInfo = matchInfo;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public Collection<ServiceConnection> getOurSpectators() {
        return this.ourSpectators;
    }

    public Collection<UUID> getBlockStateTrackerIds() {
        return this.blockStateTrackerIds;
    }

    @NotNull
    public ServiceConnection getMainSpectator() {
        return this.ourSpectators.iterator().next();
    }

    public Frame getFrame() {
        return this.frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public void loadLabyUsers(ServiceRegistry registry) {
        try {
            this.loadLabyUsers(registry, this.ourSpectators.iterator().next().getAuthentication());
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void loadLabyUsers(ServiceRegistry registry, UserAuthentication authentication) throws InterruptedException {
        /*Preconditions.checkArgument(authentication.isLoggedIn(), "tried to load laby users with an authentication that is not logged in");
        LabyConnection labyConnection = new LabyConnection(registry, authentication);
        labyConnection.connect();

        Thread.sleep(1000);

        UUID[] request = this.teams.stream().flatMap(team -> team.getMembers().stream()).map(ClanWarMember::getUniqueId).toArray(UUID[]::new);
        UserData[] data = labyConnection.requestUserData(request).getUninterruptedly(10, TimeUnit.SECONDS);

        this.labyUsers = data == null ? Collections.emptyList() : Collections.unmodifiableCollection(Arrays.stream(data).map(UserData::getUniqueId).collect(Collectors.toList()));

        labyConnection.close();*/
    }

    public Collection<ClanWarTeam> getTeams() {
        return this.teams;
    }

    public ClanWarTeam getTeam(UUID playerId) {
        return this.teams.stream().filter(team -> team.getMembers().stream().anyMatch(member -> member.getUniqueId().equals(playerId))).findFirst().orElse(null);
    }

    public RunningClanWarInfo getInfo() {
        return this.info;
    }

    public MatchInfo getMatchInfo() {
        return this.matchInfo;
    }

    public Collection<UUID> getLabyUsers() {
        return this.labyUsers;
    }

    public Collection<UUID> getBacUsers() {
        return this.matchInfo.getBacPlayers();
    }

    public boolean isLabyModUser(UUID uniqueId) {
        return this.labyUsers.contains(uniqueId);
    }

    public boolean isBacUser(UUID uniqueId) {
        return this.matchInfo.getBacPlayers().contains(uniqueId);
    }

    @Override
    public String toString() {
        return "RunningClanWar{" +
                "spectators=" + spectators +
                ", teams=" + teams +
                ", info=" + info +
                ", matchInfo=" + matchInfo +
                ", labyUsers=" + labyUsers +
                '}';
    }
}
