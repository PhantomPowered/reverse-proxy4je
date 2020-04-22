package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.block.Particle;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWorldParticles implements Packet {

    private Particle particle;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float particleSpeed;
    private int particleCount;
    private boolean longDistance;

    /**
     * These are the block/item ids and possibly metaData ids that are used to color or texture the particle.
     */
    private int[] particleArguments;

    public PacketPlayServerWorldParticles(Particle particle, float xCoord, float yCoord, float zCoord, float xOffset, float yOffset, float zOffset, float particleSpeed, int particleCount, boolean longDistance, int[] particleArguments) {
        this.particle = particle;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.particleSpeed = particleSpeed;
        this.particleCount = particleCount;
        this.longDistance = longDistance;
        this.particleArguments = particleArguments;
    }

    public PacketPlayServerWorldParticles() {
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.particle = Particle.getById(buf.readInt());

        if (this.particle == null) {
            this.particle = Particle.BARRIER;
        }

        this.longDistance = buf.readBoolean();
        this.xCoord = buf.readFloat();
        this.yCoord = buf.readFloat();
        this.zCoord = buf.readFloat();
        this.xOffset = buf.readFloat();
        this.yOffset = buf.readFloat();
        this.zOffset = buf.readFloat();
        this.particleSpeed = buf.readFloat();
        this.particleCount = buf.readInt();
        int i = this.particle.getArgumentCount();
        this.particleArguments = new int[i];

        for (int j = 0; j < i; ++j) {
            this.particleArguments[j] = buf.readVarInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeInt(this.particle.getParticleId());
        buf.writeBoolean(this.longDistance);
        buf.writeFloat(this.xCoord);
        buf.writeFloat(this.yCoord);
        buf.writeFloat(this.zCoord);
        buf.writeFloat(this.xOffset);
        buf.writeFloat(this.yOffset);
        buf.writeFloat(this.zOffset);
        buf.writeFloat(this.particleSpeed);
        buf.writeInt(this.particleCount);
        int i = this.particle.getArgumentCount();

        for (int j = 0; j < i; ++j) {
            buf.writeVarInt(this.particleArguments[j]);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WORLD_PARTICLES;
    }
}
