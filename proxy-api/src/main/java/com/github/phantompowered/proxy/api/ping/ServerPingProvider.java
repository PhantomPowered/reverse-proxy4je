package com.github.phantompowered.proxy.api.ping;

import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.task.Task;

public interface ServerPingProvider {

    // TODO this could be useful for a "ping <address>" command or automatic connects to servers to check whether they are full or not

    Task<ServerPing> pingServer(NetworkAddress address);

    Task<ServerPing> pingServer(NetworkAddress address, int protocol);

}
