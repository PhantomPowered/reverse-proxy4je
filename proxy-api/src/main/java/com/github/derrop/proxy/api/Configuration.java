package com.github.derrop.proxy.api;

public interface Configuration {

    void load();

    void save();

    int getProxyPort();

    void setProxyPort(int proxyPort);

    int getWebPort();

    void setWebPort(int webPort);

}
