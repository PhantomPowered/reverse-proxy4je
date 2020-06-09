package com.github.derrop.proxy.plugins.gomme.web;

import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.match.MatchTeam;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MatchFileHandler extends WebFileHandler {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private final MatchManager matchManager;

    public MatchFileHandler(String pathPrefix, MatchManager matchManager) {
        super(pathPrefix);
        this.matchManager = matchManager;
    }

    @Override
    public String finalLoadFile(String path, Map<String, String> queryParameters) {
        String matchId = queryParameters.get("matchId");
        if (matchId == null) {
            return null;
        }


        MatchInfo matchInfo = this.matchManager.getMatch(matchId);
        if (matchInfo == null) {
            matchInfo = new MatchInfo(this.matchManager.getCore().getRegistry().getProviderUnchecked(ServiceConnector.class).findBestConnection(null), GommeGameMode.CORES, matchId);
            this.matchManager.createMatch(matchInfo);
            //return null;
        }
        if (matchInfo.getTeams().isEmpty()) {
            matchInfo.getTeams().add(new MatchTeam(MessageType.TEAM_RED, Arrays.asList(UUID.fromString("94a1c44a-82a1-3dba-bd06-1c16fcbe9582"))));
        }


        return super.loadFile("web/matches/index.html")
                .replace("${internal.liveupdate}", String.valueOf(!matchInfo.hasEnded()))
                .replace("${match.type}", matchInfo.getGameMode().getDisplayName())
                .replace("${match.id}", matchInfo.getMatchId())
                .replace("${match.map}", "TODO Map is not implemented yet")
                .replace("${match.begin}", matchInfo.getBeginTimestamp() <= 0 ? "future" : FORMAT.format(matchInfo.getBeginTimestamp()))
                .replace("${match.end}", matchInfo.getEndTimestamp() <= 0 ? "future" : FORMAT.format(matchInfo.getEndTimestamp()))
                .replace("${match.teams}", this.getTeams(matchInfo))
                .replace("${match.events}", this.getEvents(matchInfo));
    }

    private String getEvents(MatchInfo matchInfo) {
        StringBuilder events = new StringBuilder();
        for (MatchEvent event : matchInfo.getEvents()) {
            String htmlEvent = super.loadFile("web/matches/matchEvent.html")
                    .replace("${event.time}", TIME_FORMAT.format(event.getTimestamp()))
                    .replace("${event.text}", event.toPlainText());

            events.append(htmlEvent);
        }
        return events.toString();
    }

    private String getTeams(MatchInfo matchInfo) {
        StringBuilder teams = new StringBuilder();

        for (MatchTeam team : matchInfo.getTeams()) {
            String[] players = team.getPlayers().stream()
                    .map(uniqueId -> matchInfo.getInvoker().getWorldDataProvider().getOnlinePlayer(uniqueId))
                    .filter(Objects::nonNull)
                    .map(playerInfo -> {
                        String htmlPlayer = super.loadFile("web/matches/matchPlayer.html")
                                .replace("${player.name}", playerInfo.getUsername());

                        //if (playerInfo.isUsingBAC()) { // TODO implement BAC detection
                        htmlPlayer = htmlPlayer.replace("${player.bac}", super.loadFile("web/matches/bacUser.html"));
                        //}

                        return htmlPlayer;
                    })
                    .toArray(String[]::new);

            if (players.length == 0) {
                continue;
            }

            String htmlTeam = super.loadFile("web/matches/matchTeam.html")
                    .replace("${team.name}", team.getType().name())
                    .replace("${players.first}", players[0])
                    .replace("${players.others}", String.join("\n", Arrays.copyOfRange(players, 1, players.length)));

            teams.append(htmlTeam);
        }

        return teams.toString();
    }

}
