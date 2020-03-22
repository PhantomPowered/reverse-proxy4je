package de.derrop.minecraft.proxy.connection.velocity;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.cache.packet.entity.DestroyEntities;
import de.derrop.minecraft.proxy.connection.cache.packet.entity.PositionedPacket;
import de.derrop.minecraft.proxy.connection.cache.packet.entity.SpawnPlayer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Arrays;

public class PlayerVelocityHandler {

    private ConnectedProxyClient proxyClient;

    public PlayerVelocityHandler(ConnectedProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    private boolean onGround = true;

    private int ridingEntityId;
    private boolean ridingEntity;

    private float rotPitch = 90, rotYaw = 0;
    private int motionX, motionY, motionZ;

    private double posX, posY, posZ;

    private int positionUpdateTicks = 0;

    private float lastReportedRotPitch = 90, lastReportedRotYaw = 0;

    private double lastReportedPosX, lastReportedPosY, lastReportedPosZ;

    public void handlePacket(ProtocolConstants.Direction direction, DefinedPacket packet) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            if (packet instanceof EntityVelocity) {
                this.motionX = ((EntityVelocity) packet).getMotionX();
                this.motionY = ((EntityVelocity) packet).getMotionY();
                this.motionZ = ((EntityVelocity) packet).getMotionZ();
            } else if (packet instanceof EntityAttach) {
                this.ridingEntityId = ((EntityAttach) packet).getVehicleEntityId();
                this.ridingEntity = true;
            } else if (packet instanceof DestroyEntities) {
                if (Arrays.stream(((DestroyEntities) packet).getEntityIds()).anyMatch(i -> i == this.ridingEntityId)) {
                    this.ridingEntity = false;
                }
            } else if (packet instanceof SpawnPlayer) {
                if (((PositionedPacket) packet).getEntityId() == this.proxyClient.getEntityId()) {
                    this.posX = ((PositionedPacket) packet).getX();
                    this.posY = ((PositionedPacket) packet).getY();
                    this.posZ = ((PositionedPacket) packet).getZ();
                    this.rotYaw = ((PositionedPacket) packet).getYaw();
                    this.rotPitch = ((PositionedPacket) packet).getPitch();
                }
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
            } else if (packet instanceof PlayerPosLook) {
                this.posX = ((PlayerPosLook) packet).getX();
                this.posY = ((PlayerPosLook) packet).getY();
                this.posZ = ((PlayerPosLook) packet).getZ();
                this.rotPitch = ((PlayerPosLook) packet).getPitch();
                this.rotYaw = ((PlayerPosLook) packet).getYaw();
                this.onGround = ((PlayerPosLook) packet).isOnGround();
            }
        }
    }

    public static void start() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                for (ConnectedProxyClient onlineClient : MCProxy.getInstance().getOnlineClients()) {
                    PlayerVelocityHandler velocityHandler = onlineClient.getVelocityHandler();

                    ++velocityHandler.positionUpdateTicks;

                    double d0 = velocityHandler.posX - velocityHandler.lastReportedPosX;
                    double d1 = velocityHandler.posY - velocityHandler.lastReportedPosY;
                    double d2 = velocityHandler.posZ - velocityHandler.lastReportedPosZ;
                    double d3 = velocityHandler.rotYaw - velocityHandler.lastReportedRotYaw;
                    double d4 = velocityHandler.rotPitch - velocityHandler.lastReportedRotPitch;
                    boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || velocityHandler.positionUpdateTicks >= 20;
                    boolean flag3 = d3 != 0.0D || d4 != 0.0D;

                    if (!velocityHandler.ridingEntity) {
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
                    }

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
        }).start();
    }

}
