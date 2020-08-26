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
package com.github.phantompowered.proxy.connection.velocity;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.connection.BasicServiceConnection;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityVelocity;

public class PlayerVelocityHandler {

    private static final double DRAG = 0.02;
    private final ConnectedProxyClient proxyClient;
    private double motionX;
    private double motionY;
    private double motionZ;
    private double targetX;
    private double targetY;
    private double targetZ;

    public PlayerVelocityHandler(ConnectedProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    public static void start(ServiceRegistry registry) {
        APIUtil.EXECUTOR_SERVICE.execute(() -> {
            while (!Thread.interrupted()) {
                for (ServiceConnection onlineClient : registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients()) {
                    if (!(onlineClient instanceof BasicServiceConnection) || onlineClient.getPlayer() != null) {
                        continue;
                    }
                    ConnectedProxyClient client = ((BasicServiceConnection) onlineClient).getClient();
                    Location pos = onlineClient.getLocation();

                    PlayerVelocityHandler velocityHandler = client.getVelocityHandler();

                    /*double diffX = 0;
                    double diffY = 0;
                    double diffZ = 0;

                    double x = Math.abs(pos.getX() - velocityHandler.targetX);
                    double y = Math.abs(pos.getY() - velocityHandler.targetY);
                    double z = Math.abs(pos.getZ() - velocityHandler.targetZ);

                    if (x > 0) {
                        diffX = velocityHandler.targetX < 0 ? -DRAG : DRAG;
                        if (x < DRAG) {
                            diffX = velocityHandler.targetX < 0 ? -x : x;
                        }
                    }
                    if (y > 0) {
                        diffY = velocityHandler.targetY < 0 ? -DRAG : DRAG;
                        if (z < DRAG) {
                            diffY = velocityHandler.targetY < 0 ? -y : y;
                        }
                    }
                    if (z > 0) {
                        diffZ = velocityHandler.targetZ < 0 ? -DRAG : DRAG;
                        if (z < DRAG) {
                            diffZ = velocityHandler.targetZ < 0 ? -z : z;
                        }
                    }
                    System.out.println(diffX + " " + diffY + " " + diffZ);*/
                    //client.write(new PacketPlayClientPosition(new Location(pos.getX() + diffX, pos.getY() + diffY, pos.getZ() + diffZ, pos.getYaw(), pos.getPitch())));

                    /*double d0 = velocityHandler.posX - velocityHandler.lastReportedPosX;
                    double d1 = velocityHandler.posY - velocityHandler.lastReportedPosY;
                    double d2 = velocityHandler.posZ - velocityHandler.lastReportedPosZ;
                    double d3 = velocityHandler.rotYaw - velocityHandler.lastReportedRotYaw;
                    double d4 = velocityHandler.rotPitch - velocityHandler.lastReportedRotPitch;
                    boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || velocityHandler.positionUpdateTicks >= 20;
                    boolean flag3 = d3 != 0.0D || d4 != 0.0D;*/

                    onlineClient.teleport(new Location(
                            //pos.getX() + diffX, pos.getY() + diffY, pos.getZ() + diffZ,
                            pos.getX() + (velocityHandler.motionX / 20),
                            pos.getY() + (velocityHandler.motionY / 20),
                            pos.getZ() + (velocityHandler.motionZ / 20),
                            pos.getYaw(),
                            pos.getPitch()
                    ));

                    /*if (velocityHandler.ridingEntity) {
                        if (flag2 && flag3) {
                            onlineClient.getChannelWrapper().write(new PlayerPosLook(velocityHandler.posX, velocityHandler.posY, velocityHandler.posZ, velocityHandler.rotYaw, velocityHandler.rotPitch, velocityHandler.onGround));
                        } else if (flag2) {
                            onlineClient.getChannelWrapper().write(new PlayerPosition(velocityHandler.posX, velocityHandler.posY, velocityHandler.posZ, velocityHandler.onGround));
                        } else if (flag3) {
                            onlineClient.getChannelWrapper().write(new PlayerLook(velocityHandler.rotYaw, velocityHandler.rotPitch, velocityHandler.onGround));
                        } else {
                            onlineClient.getChannelWrapper().write(new Player(velocityHandler.onGround));
                        }
                    } else {
                        onlineClient.getChannelWrapper().write(new PlayerPosLook(velocityHandler.motionX, -999.0D, velocityHandler.motionZ, velocityHandler.rotYaw, velocityHandler.rotPitch, velocityHandler.onGround));
                        flag2 = false;
                    }*/

                    /*if (flag2) {
                        velocityHandler.lastReportedPosX = velocityHandler.posX;
                        velocityHandler.lastReportedPosY = velocityHandler.posY;
                        velocityHandler.lastReportedPosZ = velocityHandler.posZ;
                        velocityHandler.positionUpdateTicks = 0;
                    }

                    if (flag3) {
                        velocityHandler.lastReportedRotYaw = velocityHandler.rotYaw;
                        velocityHandler.lastReportedRotPitch = velocityHandler.rotPitch;
                    }*/

                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void handlePacket(ProtocolDirection direction, Packet packet) {
        if (packet instanceof PacketPlayServerEntityVelocity) {
            PacketPlayServerEntityVelocity velocity = (PacketPlayServerEntityVelocity) packet;
            if (velocity.getEntityId() != this.proxyClient.getEntityId()) {
                return;
            }

            this.motionX = velocity.getMotionX() / 8000D;
            this.motionY = velocity.getMotionY() / 8000D;
            this.motionZ = velocity.getMotionZ() / 8000D;

            Location pos = this.proxyClient.getConnection().getLocation();
            this.targetX = pos.getX() + this.motionX;
            this.targetY = pos.getY() + this.motionY;
            this.targetZ = pos.getZ() + this.motionZ;
        }
    }

    public void moveEntity(double x, double y, double z) {
        /*if (this.inWeb) {
            this.inWeb = false;
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;
            this.motionX = 0.0F;
            this.motionY = 0.0F;
            this.motionZ = 0.0F;
        }*/

        // todo implement with material?
        //int material = ((ChunkCache) this.proxyClient.getPacketCache().getHandler(handler -> handler instanceof ChunkCache)).getMaterial(new BlockPos(x, y - 1, z));
        //this.onGround = (material != 0 && material != 10 /* flowing lava */ && material != 11 /* stationary lava */ &&
        //        material != 8 /* flowing water */ && material != 9 /* stationary water */) && (int) y == y;
        //   this.onGround = (int) y == y;
    }

}
