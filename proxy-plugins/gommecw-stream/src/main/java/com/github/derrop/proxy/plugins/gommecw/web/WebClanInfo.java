package com.github.derrop.proxy.plugins.gommecw.web;

public class WebClanInfo {

    private final String name;
    /*private final String shortcut;
    private final int eloPoints;*/

    public WebClanInfo(String name) {
        this.name = name;
    }

    /*public WebClanInfo(String name, String shortcut, int eloPoints) {
        this.name = name;
        this.shortcut = shortcut;
        this.eloPoints = eloPoints;
    }*/

    public String getName() {
        return this.name;
    }

    /*public String getShortcut() {
        return this.shortcut;
    }

    public int getEloPoints() {
        return this.eloPoints;
    }*/

    @Override
    public String toString() {
        return "WebClanInfo{"
                + "name='" + name + '\''
                + '}';
    }
}
