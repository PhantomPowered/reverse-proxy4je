/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.connection.reconnect;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServiceReconnectionHandler implements Runnable {

    public static final long INTERVAL = TimeUnit.SECONDS.toMillis(20);

    private final ServiceRegistry registry;

    public ServiceReconnectionHandler(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        ServiceConnector connector = this.registry.getProviderUnchecked(ServiceConnector.class);

        for (ServiceConnection client : connector.getOnlineClients()) {
            if (client.isConnected() || client.getLastConnectionTryTimestamp() == -1) {
                continue;
            }

            long diff = System.currentTimeMillis() - client.getLastConnectionTryTimestamp();
            if (diff < INTERVAL) {
                continue;
            }

            try {
                client.connect().get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
