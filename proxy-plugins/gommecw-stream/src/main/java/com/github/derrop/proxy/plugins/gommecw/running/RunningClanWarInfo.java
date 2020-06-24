package com.github.derrop.proxy.plugins.gommecw.running;

import com.github.derrop.proxy.plugins.gommecw.web.WebClanInfo;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class RunningClanWarInfo {

    private final WebClanInfo[] clans;
    private String matchId;
    private String map;
    private long beginTimestamp = 0;

    public RunningClanWarInfo(WebClanInfo[] clans) {
        Preconditions.checkArgument(clans.length == 2, "clans.length != 2");

        this.clans = clans;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setBeginTimestamp(long beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    public String getMap() {
        return this.map;
    }

    public WebClanInfo[] getClans() {
        return this.clans;
    }

    public long getBeginTimestamp() {
        return this.beginTimestamp;
    }

    @Override
    public String toString() {
        return "RunningClanWarInfo{" +
                "matchId='" + matchId + '\'' +
                ", map='" + map + '\'' +
                ", clans=" + Arrays.toString(clans) +
                ", beginTimestamp=" + beginTimestamp +
                '}';
    }
}
