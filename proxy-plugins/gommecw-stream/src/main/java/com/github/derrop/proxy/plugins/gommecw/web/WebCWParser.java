package com.github.derrop.proxy.plugins.gommecw.web;

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
import java.util.Collection;

public class WebCWParser {

    private static final String URL = "https://www.gommehd.net/clans/get-matches?game=bedwars&finished=%b&amount=10";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public RunningClanWarInfo[] parseClanWars(boolean running) throws IOException, ParseException {
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
                } else if (current != null && element.tagName().equals("a")) {
                    current.setMatchId(element.attr("href").replaceFirst("/clan-match\\?id=", ""));
                    infos.add(current);
                }
            }

            return infos.toArray(new RunningClanWarInfo[0]);
        }
    }

}
