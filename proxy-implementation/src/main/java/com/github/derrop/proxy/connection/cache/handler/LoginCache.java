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

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityStatus;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerRespawn;

public class LoginCache implements PacketCacheHandler {

    private PacketPlayServerLogin lastLogin;
    private PacketPlayServerRespawn lastRespawn;

    public PacketPlayServerLogin getLastLogin() {
        return lastLogin;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.LOGIN, ProtocolIds.ToClient.Play.RESPAWN};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        if (!(newPacket instanceof PacketPlayServerLogin)) {
            return;
        }
        this.lastLogin = (PacketPlayServerLogin) newPacket;
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (this.lastLogin == null) {
            Constants.EXECUTOR_SERVICE.execute(() -> {
                int count = 0;
                while (this.lastLogin == null && count++ < 50) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }

                if (count >= 50) {
                    return;
                }
                this.sendCached(con, targetProxyClient);
            });
            return;
        }

        if (con instanceof Player) {
            Player player = (Player) con;
            if (player.getConnectedClient() == null) {
                PacketPlayServerLogin login = new PacketPlayServerLogin(
                        this.lastLogin.getEntityId(),
                        (short) targetProxyClient.getConnection().getWorldDataProvider().getOwnGameMode().getId(),
                        targetProxyClient.getDimension(),
                        this.lastLogin.getDifficulty(),
                        this.lastRespawn != null ? this.lastRespawn.getDifficulty() : this.lastLogin.getDifficulty(),
                        this.lastRespawn != null ? this.lastRespawn.getLevelType() : this.lastLogin.getLevelType(),
                        this.lastLogin.isReducedDebugInfo()
                );
                player.sendPacket(login);

                return;
            }

            PacketPlayServerEntityStatus entityStatus = new PacketPlayServerEntityStatus(
                    targetProxyClient.getEntityId(),
                    (byte) (this.lastLogin.isReducedDebugInfo() ? 22 : 23)
            );
            player.sendPacket(entityStatus);
        }

        /*if (!(con instanceof Player) || this.lastLogin.getDimension() == ((Player) con).getDimension()) {
            con.sendPacket(new PacketPlayServerRespawn(
                    (this.lastLogin.getDimension() >= 0 ? -1 : 0),
                    this.lastLogin.getDifficulty(),
                    this.lastLogin.getGameMode(),
                    this.lastLogin.getLevelType()
            ));
        }*/

        con.sendPacket(new PacketPlayServerRespawn(
                targetProxyClient.getDimension(),
                this.lastRespawn != null ? this.lastRespawn.getDifficulty() : this.lastLogin.getDifficulty(),
                (short) targetProxyClient.getConnection().getWorldDataProvider().getOwnGameMode().getId(),
                this.lastRespawn != null ? this.lastRespawn.getLevelType() : this.lastLogin.getLevelType()
        ));

    }

}
