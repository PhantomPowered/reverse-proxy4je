package com.github.derrop.proxy.ping;

import com.github.derrop.proxy.api.network.NetworkAddress;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.ping.ServerPingProvider;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.connection.handler.ServerPinger;

public class DefaultServerPingProvider implements ServerPingProvider {

    private final ServiceRegistry serviceRegistry;

    public DefaultServerPingProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Task<ServerPing> pingServer(NetworkAddress address) {
        return new ServerPinger(this.serviceRegistry).ping(address);
    }

    @Override
    public Task<ServerPing> pingServer(NetworkAddress address, int protocol) {
        return new ServerPinger(this.serviceRegistry).protocol(protocol).ping(address);
    }
}
