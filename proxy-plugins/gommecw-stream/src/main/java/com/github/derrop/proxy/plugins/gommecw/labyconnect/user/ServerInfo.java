package com.github.derrop.proxy.plugins.gommecw.labyconnect.user;

public class ServerInfo {
    private final String serverIp;
    private final int serverPort;
    private final String specifiedServerName;

    public ServerInfo(String serverIp, int serverPort, String specifiedServerName) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.specifiedServerName = specifiedServerName;
    }

    public ServerInfo(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.specifiedServerName = null;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public String getSpecifiedServerName() {
        return this.specifiedServerName;
    }

    public boolean isServerAvailable() {
        return (this.serverIp != null && !this.serverIp.replaceAll(" ", "").isEmpty());
    }

    public String getDisplayAddress() {
        String formattedIp = this.serverIp;
        if (formattedIp.endsWith(".")) {
            formattedIp = formattedIp.substring(0, formattedIp.length() - 1);
        }
        if (this.serverPort != 25565) {
            formattedIp = formattedIp + ":" + this.serverPort;
        }
        return formattedIp;
    }
}


