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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerCamera;

public class CameraCache implements PacketCacheHandler { // TODO if you go into the camera of another player (spectator mode) and rejoin, you will be stuck in the login screen

    private PacketPlayServerCamera camera;

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CAMERA};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.camera = (PacketPlayServerCamera) newPacket.getDeserializedPacket();
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.camera == null) {
            return;
        }
        Constants.EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            con.sendPacket(camera);
        });
    }
}
