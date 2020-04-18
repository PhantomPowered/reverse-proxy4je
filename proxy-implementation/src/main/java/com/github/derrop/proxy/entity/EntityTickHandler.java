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
package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.connection.reconnect.ReconnectProfile;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;

import java.util.concurrent.TimeUnit;

public final class EntityTickHandler {

    private EntityTickHandler() {
        throw new UnsupportedOperationException();
    }

    public static void startTick() {
        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            for (BasicServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
                if (onlineClient.getPlayer() != null) {
                    onlineClient.getPlayer().sendPacket(new PacketPlayKeepAlive(System.nanoTime())); // TODO: wait for result (if no, disconnect)
                }
            }

            for (ReconnectProfile value : MCProxy.getInstance().getReconnectProfiles().values()) {
                if (System.currentTimeMillis() >= value.getTimeout()) {
                    MCProxy.getInstance().getReconnectProfiles().remove(value.getUniqueId());
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
