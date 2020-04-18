package com.github.derrop.proxy.api.ping;

import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.util.NetworkAddress;

public interface ServerPingProvider {

    // TODO this could be useful for a "ping <address>" command or automatic connects to servers to check whether they are full or not

    Task<ServerPing> pingServer(NetworkAddress address);

    Task<ServerPing> pingServer(NetworkAddress address, int protocol);

}
