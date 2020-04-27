package com.github.derrop.proxy.plugins.gomme.secret;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;
import com.github.derrop.proxy.api.scoreboard.Team;

public class GommeNickProfile {

    private ServiceConnection invoker;
    private String realName;
    private Team realTeam;
    private String nickName; // this might not be correct
    private PlayerInfo nickInfo; // this might not be correct

    public GommeNickProfile(ServiceConnection invoker, String realName, Team realTeam) {
        this.invoker = invoker;
        this.realName = realName;
        this.realTeam = realTeam;
    }

    public ServiceConnection getInvoker() {
        return invoker;
    }

    public String getRealName() {
        return realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setRealTeam(Team realTeam) {
        this.realTeam = realTeam;
    }

    public Team getRealTeam() {
        return realTeam;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public PlayerInfo getNickInfo() {
        return nickInfo;
    }

    public void setNickInfo(PlayerInfo nickInfo) {
        this.nickInfo = nickInfo;
    }

    @Override
    public String toString() {
        return "GommeNickProfile{" +
                "realName='" + realName + '\'' +
                ", realTeam=" + realTeam +
                ", nickName='" + nickName + '\'' +
                ", nickInfo=" + nickInfo +
                '}';
    }
}
