package com.github.derrop.proxy.ping;

import com.github.derrop.proxy.launcher.MCProxy;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.ping.ServerPingProvider;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.network.NetworkAddress;
import com.github.derrop.proxy.connection.handler.ServerPinger;

public class DefaultServerPingProvider implements ServerPingProvider {

    private final MCProxy proxy;

    public DefaultServerPingProvider(MCProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Task<ServerPing> pingServer(NetworkAddress address) {
        return new ServerPinger().ping(this.proxy, address);
    }

    @Override
    public Task<ServerPing> pingServer(NetworkAddress address, int protocol) {
        return new ServerPinger().protocol(protocol).ping(this.proxy, address);
    }
}
