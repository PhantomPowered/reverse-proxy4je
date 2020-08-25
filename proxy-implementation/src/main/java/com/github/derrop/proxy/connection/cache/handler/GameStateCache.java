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

import com.github.derrop.proxy.api.math.MathHelper;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.player.GameMode;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.player.PacketPlayServerGameStateChange;

public class GameStateCache implements PacketCacheHandler {

    private boolean raining;
    private float rainStrength; // 0 = disabled; 1 = maximum sent by the server

    private float thunderStrength; // 0 = disabled; 1 = maximum sent by the server
    private GameMode gameMode;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.GAME_STATE_CHANGE};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        PacketPlayServerGameStateChange packet = (PacketPlayServerGameStateChange) newPacket;

        if (packet.getState() == 1) { // enable raining
            this.raining = true;
            this.rainStrength = 0;
        } else if (packet.getState() == 2) { // disable raining
            this.raining = false;
            this.rainStrength = 1;
        } else if (packet.getState() == 3) {
            this.gameMode = GameMode.getById(MathHelper.floor(packet.getValue() + 0.5F));
        /*} else if (packet.getState() == 4) { // display win game screen
        } else if (packet.getState() == 5) { // some demo actions (display demo screen, display some messages)
        } else if (packet.getState() == 6) { // play "random.successful_hit" sound
        */
        } else if (packet.getState() == 7) { // change rain strength
            this.rainStrength = packet.getValue();
        } else if (packet.getState() == 8) { // change thunder strength
            this.thunderStrength = packet.getValue();
        }
        /* else if (packet.getState() == 10) { // display guardian effect
        }*/
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (this.raining) {
            con.sendPacket(new PacketPlayServerGameStateChange(1, 0));
            con.sendPacket(new PacketPlayServerGameStateChange(1, this.rainStrength));
        }

        if (this.gameMode != null) {
            con.sendPacket(new PacketPlayServerGameStateChange(3, this.gameMode.getId()));
        }

        if (this.thunderStrength != 0) {
            con.sendPacket(new PacketPlayServerGameStateChange(8, this.thunderStrength));
        }
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean isThundering() {
        return this.thunderStrength != 0;
    }

    public float getThunderStrength() {
        return this.thunderStrength;
    }

    public float getRainStrength() {
        return this.rainStrength;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

}
