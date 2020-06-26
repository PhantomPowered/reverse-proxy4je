package com.github.derrop.proxy.plugins.gommecw.web;

import com.github.derrop.proxy.api.Tickable;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gommecw.event.GommeCWAddEvent;
import com.github.derrop.proxy.plugins.gommecw.event.GommeCWRemoveEvent;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class WebCWParser implements Tickable {

    private static final int UPDATE_TICK = 100;

    private static final String URL = "https://www.gommehd.net/clans/get-matches?game=bedwars&finished=%b&amount=100";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final ServiceRegistry registry;

    private RunningClanWarInfo[] lastInfos;
    private int currentTick = 0;

    public WebCWParser(ServiceRegistry registry) {
        this.registry = registry;
    }

    public RunningClanWarInfo[] getCachedRunningInfos() {
        return this.lastInfos == null ? new RunningClanWarInfo[0] : this.lastInfos;
    }

    public Optional<RunningClanWarInfo> getCachedRunningInfo(String matchId) {
        return Arrays.stream(this.getCachedRunningInfos())
                .filter(info -> info.getMatchId().equals(matchId))
                .findFirst();
    }

    public RunningClanWarInfo[] parseRunningClanWars() {
        return this.parseClanWars(true);
    }

    public RunningClanWarInfo[] parseFinishedClanWars() {
        return this.parseClanWars(false);
    }

    public RunningClanWarInfo[] parseClanWars(boolean running) {
        try {
            return this.parseClanWars0(running);
        } catch (IOException | ParseException exception) {
            exception.printStackTrace();
        }
        return new RunningClanWarInfo[0];
    }

    public RunningClanWarInfo[] parseClanWars0(boolean running) throws IOException, ParseException {
        String url = String.format(URL, !running);
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");

        try (InputStream inputStream = connection.getInputStream()) {
            Document document = Jsoup.parse(inputStream, "UTF-8", url);

            Collection<RunningClanWarInfo> infos = new ArrayList<>();

            RunningClanWarInfo current = null;
            for (Element element : document.getAllElements()) {
                if (element.tagName().equals("div")) {
                    Elements a = element.getElementsByTag("a");
                    current = new RunningClanWarInfo(new WebClanInfo[]{
                            new WebClanInfo(a.first().text()),
                            new WebClanInfo(a.last().text())
                    });
                } else if (current != null && element.tagName().equals("span")) {
                    switch (element.className()) {
                        case "date":
                            current.setBeginTimestamp(current.getBeginTimestamp() + DATE_FORMAT.parse(element.text()).getTime());
                            break;
                        case "time":
                            current.setBeginTimestamp(current.getBeginTimestamp() + TIME_FORMAT.parse(element.text()).getTime());
                            break;
                        case "":
                            if (current.getMap() == null) {
                                current.setMap(element.text());
                            }
                            break;
                    }
                } else if (current != null && element.tagName().equals("a") && element.text().equals("Matchpage")) {
                    String matchId = element.attr("href").replaceFirst("/clan-match\\?id=", "");
                    current.setMatchId(matchId);
                    if (infos.stream().noneMatch(info -> matchId.equals(info.getMatchId()))) {
                        infos.add(current);
                    }
                    current = null;
                }
            }

            return infos.toArray(new RunningClanWarInfo[0]);
        }
    }

    public void beginClanWar(RunningClanWarInfo info) {
        this.registry.getProviderUnchecked(EventManager.class).callEvent(new GommeCWAddEvent(info));
    }

    public void endClanWar(RunningClanWarInfo info) {
        this.registry.getProviderUnchecked(EventManager.class).callEvent(new GommeCWRemoveEvent(info));
    }

    @Override
    public void handleTick() {
        if (this.currentTick++ > UPDATE_TICK) {
            this.currentTick = 0;

            RunningClanWarInfo[] infos = this.parseRunningClanWars();

            if (this.lastInfos == null) {
                this.lastInfos = infos;
                return;
            }

            /*Arrays.stream(this.lastInfos)
                    .filter(info -> Arrays.stream(infos).anyMatch(newInfo -> newInfo.getMatchId().equals(info.getMatchId())))
                    .forEach(this::endClanWar);

            Arrays.stream(infos)
                    .filter(newInfo -> Arrays.stream(this.lastInfos).anyMatch(info -> info.getMatchId().equals(newInfo.getMatchId())))
                    .forEach(this::beginClanWar);*/
        }
    }
}
