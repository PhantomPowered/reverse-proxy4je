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

import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerWorldBorder;
import com.github.derrop.proxy.api.network.PacketSender;

public class WorldBorderCache implements PacketCacheHandler {

    private PacketPlayServerWorldBorder worldBorder;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.WORLD_BORDER};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        PacketPlayServerWorldBorder border = (PacketPlayServerWorldBorder) newPacket.getDeserializedPacket();

        if (border.getAction() == PacketPlayServerWorldBorder.Action.INITIALIZE) {
            this.worldBorder = border;
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.LERP_SIZE) {
            this.worldBorder.setDiameter(border.getDiameter());
            this.worldBorder.setTargetSize(border.getTargetSize());
            this.worldBorder.setTimeUntilTarget(border.getTimeUntilTarget());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_SIZE) {
            this.worldBorder.setSize(border.getSize());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_CENTER) {
            this.worldBorder.setCenterX(border.getCenterX());
            this.worldBorder.setCenterZ(border.getCenterZ());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_WARNING_BLOCKS) {
            this.worldBorder.setWarningDistance(border.getWarningDistance());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_WARNING_TIME) {
            this.worldBorder.setWarningTime(border.getWarningTime());
        }
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (this.worldBorder != null) {
            con.sendPacket(this.worldBorder);
        }
    }
}
