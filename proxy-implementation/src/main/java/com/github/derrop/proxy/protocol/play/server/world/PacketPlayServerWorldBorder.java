package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerWorldBorder extends DefinedPacket {

    private Action action;
    private int size;
    private double centerX;
    private double centerZ;
    private double targetSize;
    private double diameter;
    private long timeUntilTarget;
    private int warningTime;
    private int warningDistance;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.action = Action.values()[readVarInt(buf)];

        switch (this.action) {
            case SET_SIZE:
                this.targetSize = buf.readDouble();
                break;

            case LERP_SIZE:
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = readVarLong(buf);
                break;

            case SET_CENTER:
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                break;

            case SET_WARNING_BLOCKS:
                this.warningDistance = readVarInt(buf);
                break;

            case SET_WARNING_TIME:
                this.warningTime = readVarInt(buf);
                break;

            case INITIALIZE:
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                this.diameter = buf.readDouble();
                this.targetSize = buf.readDouble();
                this.timeUntilTarget = readVarLong(buf);
                this.size = readVarInt(buf);
                this.warningDistance = readVarInt(buf);
                this.warningTime = readVarInt(buf);
                break;

        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeVarInt(this.action.ordinal(), buf);

        switch (this.action) {
            case SET_SIZE:
                buf.writeDouble(this.targetSize);
                break;

            case LERP_SIZE:
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                writeVarLong(this.timeUntilTarget, buf);
                break;

            case SET_CENTER:
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                break;

            case SET_WARNING_BLOCKS:
                writeVarInt(this.warningDistance, buf);
                break;

            case SET_WARNING_TIME:
                writeVarInt(this.warningTime, buf);
                break;

            case INITIALIZE:
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                buf.writeDouble(this.diameter);
                buf.writeDouble(this.targetSize);
                writeVarLong(this.timeUntilTarget, buf);
                writeVarInt(this.size, buf);
                writeVarInt(this.warningDistance, buf);
                writeVarInt(this.warningTime, buf);
                break;
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WORLD_BOARDER;
    }

    public enum Action {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS,
        ;
    }

}
