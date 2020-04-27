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
package com.github.derrop.proxy.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

public class NetworkAddress {

    private String host;
    private int port;

    public NetworkAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Nullable
    public static NetworkAddress parse(@NotNull String input) {
        String[] hostAndPort = input.split(":");
        if (hostAndPort.length == 0) {
            return null;
        }

        String host;
        if (hostAndPort[0].split("\\.").length != 4) {
            try {
                host = InetAddress.getByName(hostAndPort[0]).getHostAddress();
            } catch (final IOException ex) {
                System.err.println("Unable to resolve host " + hostAndPort[0]);
                return null;
            }
        } else {
            host = hostAndPort[0];
        }

        int port;
        if (hostAndPort.length == 1) {
            port = 25565;
        } else {
            try {
                port = Integer.parseInt(hostAndPort[1]);
            } catch (final NumberFormatException ex) {
                System.err.println("Wrong port " + hostAndPort[1]);
                return null;
            }
        }

        return new NetworkAddress(host, port);
    }

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkAddress that = (NetworkAddress) o;
        return port == that.port &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
