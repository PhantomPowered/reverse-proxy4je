package com.github.derrop.proxy.ping;

import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.ping.ServerPingProvider;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.connection.handler.ServerPinger;

public class DefaultServerPingProvider implements ServerPingProvider {
    @Override
    public Task<ServerPing> pingServer(NetworkAddress address) {
        return new ServerPinger().ping(address);
    }

    @Override
    public Task<ServerPing> pingServer(NetworkAddress address, int protocol) {
        return new ServerPinger().protocol(protocol).ping(address);
    }
}
