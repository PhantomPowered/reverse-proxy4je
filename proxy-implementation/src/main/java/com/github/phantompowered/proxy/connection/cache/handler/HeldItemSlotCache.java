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
package com.github.phantompowered.proxy.connection.cache.handler;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.PacketCacheHandler;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.client.inventory.PacketPlayClientHeldItemSlot;
import com.github.phantompowered.proxy.protocol.play.server.player.PacketPlayServerHeldItemSlot;

public class HeldItemSlotCache implements PacketCacheHandler {

    private int slot = 0;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.HELD_ITEM_SLOT, ProtocolIds.FromClient.Play.HELD_ITEM_SLOT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        if (newPacket instanceof PacketPlayServerHeldItemSlot) {
            this.slot = ((PacketPlayServerHeldItemSlot) newPacket).getSlot();
        }
    }

    @Override
    public void cacheClientPacket(PacketCache packetCache, Packet newPacket) {
        if (newPacket instanceof PacketPlayClientHeldItemSlot) {
            this.slot = ((PacketPlayClientHeldItemSlot) newPacket).getSlot();
        }
    }

    public int getSlot() {
        return this.slot;
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        con.sendPacket(new PacketPlayServerHeldItemSlot(this.slot));
    }
}
