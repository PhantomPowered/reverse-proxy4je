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
package com.github.derrop.proxy.connection.login;

import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.DefaultServiceConnector;
import com.github.derrop.proxy.network.channel.ChannelListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ProxyClientLoginListener implements ChannelListener {

    private final ConnectedProxyClient client;

    public ProxyClientLoginListener(ConnectedProxyClient client) {
        this.client = client;
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        ServiceConnector connector = this.client.getServiceRegistry().getProviderUnchecked(ServiceConnector.class);
        if (connector instanceof DefaultServiceConnector) {
            ((DefaultServiceConnector) connector).unregisterConnection(this.client.getConnection());
        }
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
    }

    @Override
    public void handleChannelActive(@NotNull NetworkChannel channel) {
        System.out.println("Connected to " + this.client.getServerAddress() + " with the account " + this.client.getAccountName());
    }

}
