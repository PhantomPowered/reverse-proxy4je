package com.github.derrop.proxy.connection.velocity;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.protocol.client.PacketC06PlayerPosLook;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerDestroyEntities;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerSpawnPlayer;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PositionedPacket;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Arrays;

public class PlayerVelocityHandler {

    private ConnectedProxyClient proxyClient;

    public PlayerVelocityHandler(ConnectedProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    private boolean onGround = true;
    private boolean inWeb = false;

    private int ridingEntityId;
    private boolean ridingEntity;

    private float rotPitch = 90, rotYaw = 0;
    private double motionX, motionY, motionZ;

    private double posX, posY, posZ;

    private int positionUpdateTicks = 0;

    private float lastReportedRotPitch = 90, lastReportedRotYaw = 0;

    private double lastReportedPosX, lastReportedPosY, lastReportedPosZ;

    public void handlePacket(ProtocolConstants.Direction direction, DefinedPacket packet) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            if (packet instanceof EntityVelocity) {
                this.motionX = ((EntityVelocity) packet).getMotionX() / 8000D;
                this.motionY = ((EntityVelocity) packet).getMotionY() / 8000D;
                this.motionZ = ((EntityVelocity) packet).getMotionZ() / 8000D;
            } else if (packet instanceof EntityAttach) {
                this.ridingEntityId = ((EntityAttach) packet).getVehicleEntityId();
                this.ridingEntity = true;
            } else if (packet instanceof PacketPlayServerDestroyEntities) {
                if (Arrays.stream(((PacketPlayServerDestroyEntities) packet).getEntityIds()).anyMatch(i -> i == this.ridingEntityId)) {
                    this.ridingEntity = false;
                }
            } else if (packet instanceof PacketPlayServerSpawnPlayer) {
                if (((PositionedPacket) packet).getEntityId() == this.proxyClient.getEntityId()) {
                    this.posX = ((PositionedPacket) packet).getX();
                    this.posY = ((PositionedPacket) packet).getY();
                    this.posZ = ((PositionedPacket) packet).getZ();
                    this.rotYaw = ((PositionedPacket) packet).getYaw();
                    this.rotPitch = ((PositionedPacket) packet).getPitch();
                }
                this.moveEntity(this.posX, this.posY, this.posZ);
            }
        } else if (direction == ProtocolConstants.Direction.TO_SERVER) {
            // todo idk if it is necessary to listen to the packets by the client for positions like they are sent below
            if (packet instanceof PlayerPosition) {
                this.posX = ((PlayerPosition) packet).getX();
                this.posY = ((PlayerPosition) packet).getY();
                this.posZ = ((PlayerPosition) packet).getZ();
                this.onGround = ((PlayerPosition) packet).isOnGround();
            } else if (packet instanceof PlayerLook) {
                this.rotPitch = ((PlayerLook) packet).getPitch();
                this.rotYaw = ((PlayerLook) packet).getYaw();
                this.onGround = ((PlayerLook) packet).isOnGround();
            } else if (packet instanceof PacketC06PlayerPosLook) {
                this.posX = ((PacketC06PlayerPosLook) packet).getX();
                this.posY = ((PacketC06PlayerPosLook) packet).getY();
                this.posZ = ((PacketC06PlayerPosLook) packet).getZ();
                this.rotPitch = ((PacketC06PlayerPosLook) packet).getPitch();
                this.rotYaw = ((PacketC06PlayerPosLook) packet).getYaw();
                this.onGround = ((PacketC06PlayerPosLook) packet).isOnGround();
            }
            this.moveEntity(this.posX, this.posY, this.posZ);
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
        this.onGround = (int) y == y;
    }

    public static void start() {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            while (!Thread.interrupted()) {
                for (BasicServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
                    if (onlineClient.getPlayer() != null) {
                        continue;
                    }

                    PlayerVelocityHandler velocityHandler = onlineClient.getClient().getVelocityHandler();

                    ++velocityHandler.positionUpdateTicks;

                    double d0 = velocityHandler.posX - velocityHandler.lastReportedPosX;
                    double d1 = velocityHandler.posY - velocityHandler.lastReportedPosY;
                    double d2 = velocityHandler.posZ - velocityHandler.lastReportedPosZ;
                    double d3 = velocityHandler.rotYaw - velocityHandler.lastReportedRotYaw;
                    double d4 = velocityHandler.rotPitch - velocityHandler.lastReportedRotPitch;
                    boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || velocityHandler.positionUpdateTicks >= 20;
                    boolean flag3 = d3 != 0.0D || d4 != 0.0D;

                    onlineClient.sendPacket(new PlayerPosition(
                            velocityHandler.posX + (velocityHandler.motionX / 20),
                            velocityHandler.posY + (velocityHandler.motionY / 20),
                            velocityHandler.posZ + (velocityHandler.motionZ / 20),
                            velocityHandler.onGround
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

                    if (flag2) {
                        velocityHandler.lastReportedPosX = velocityHandler.posX;
                        velocityHandler.lastReportedPosY = velocityHandler.posY;
                        velocityHandler.lastReportedPosZ = velocityHandler.posZ;
                        velocityHandler.positionUpdateTicks = 0;
                    }

                    if (flag3) {
                        velocityHandler.lastReportedRotYaw = velocityHandler.rotYaw;
                        velocityHandler.lastReportedRotPitch = velocityHandler.rotPitch;
                    }

                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

}
