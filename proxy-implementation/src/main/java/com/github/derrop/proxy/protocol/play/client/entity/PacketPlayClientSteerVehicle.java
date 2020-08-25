package com.github.derrop.proxy.protocol.play.client.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientSteerVehicle implements Packet {

    /* Positive for left strafe, negative for right */
    private float strafeSpeed;

    /* Positive for forward, negative for backward */
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;

    public PacketPlayClientSteerVehicle() {
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.strafeSpeed = buf.readFloat();
        this.forwardSpeed = buf.readFloat();
        byte b = buf.readByte();
        this.jumping = (b & 1) > 0;
        this.sneaking = (b & 2) > 0;
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeFloat(this.strafeSpeed);
        buf.writeFloat(this.forwardSpeed);
        byte b = 0;

        if (this.jumping) {
            b = (byte) (b | 1);
        }

        if (this.sneaking) {
            b = (byte) (b | 2);
        }

        buf.writeByte(b);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.STEER_VEHICLE;
    }
}
