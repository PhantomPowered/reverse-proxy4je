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
package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWorldBorder implements Packet {

    private Action action;
    private int size;
    private double centerX;
    private double centerZ;
    private double targetSize;
    private double diameter;
    private long timeUntilTarget;
    private int warningTime;
    private int warningDistance;

    public PacketPlayServerWorldBorder(Action action, int size, double centerX, double centerZ, double targetSize, double diameter, long timeUntilTarget, int warningTime, int warningDistance) {
        this.action = action;
        this.size = size;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.targetSize = targetSize;
        this.diameter = diameter;
        this.timeUntilTarget = timeUntilTarget;
        this.warningTime = warningTime;
        this.warningDistance = warningDistance;
    }

    public PacketPlayServerWorldBorder() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WORLD_BORDER;
    }

    public Action getAction() {
        return this.action;
    }

    public int getSize() {
        return this.size;
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public double getTargetSize() {
        return this.targetSize;
    }

    public double getDiameter() {
        return this.diameter;
    }

    public long getTimeUntilTarget() {
        return this.timeUntilTarget;
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public int getWarningDistance() {
        return this.warningDistance;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterZ(double centerZ) {
        this.centerZ = centerZ;
    }

    public void setTargetSize(double targetSize) {
        this.targetSize = targetSize;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public void setTimeUntilTarget(long timeUntilTarget) {
        this.timeUntilTarget = timeUntilTarget;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public void setWarningDistance(int warningDistance) {
        this.warningDistance = warningDistance;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[protoBuf.readVarInt()];

        switch (this.action) {
            case SET_SIZE:
                this.targetSize = protoBuf.readDouble();
                break;

            case LERP_SIZE:
                this.diameter = protoBuf.readDouble();
                this.targetSize = protoBuf.readDouble();
                this.timeUntilTarget = protoBuf.readVarLong();
                break;

            case SET_CENTER:
                this.centerX = protoBuf.readDouble();
                this.centerZ = protoBuf.readDouble();
                break;

            case SET_WARNING_BLOCKS:
                this.warningDistance = protoBuf.readVarInt();
                break;

            case SET_WARNING_TIME:
                this.warningTime = protoBuf.readVarInt();
                break;

            case INITIALIZE:
                this.centerX = protoBuf.readDouble();
                this.centerZ = protoBuf.readDouble();
                this.diameter = protoBuf.readDouble();
                this.targetSize = protoBuf.readDouble();
                this.timeUntilTarget = protoBuf.readVarLong();
                this.size = protoBuf.readVarInt();
                this.warningDistance = protoBuf.readVarInt();
                this.warningTime = protoBuf.readVarInt();
                break;

            default:
                break;
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.action.ordinal());

        switch (this.action) {
            case SET_SIZE:
                protoBuf.writeDouble(this.targetSize);
                break;

            case LERP_SIZE:
                protoBuf.writeDouble(this.diameter);
                protoBuf.writeDouble(this.targetSize);
                protoBuf.writeVarLong(this.timeUntilTarget);
                break;

            case SET_CENTER:
                protoBuf.writeDouble(this.centerX);
                protoBuf.writeDouble(this.centerZ);
                break;

            case SET_WARNING_BLOCKS:
                protoBuf.writeVarInt(this.warningDistance);
                break;

            case SET_WARNING_TIME:
                protoBuf.writeVarInt(this.warningTime);
                break;

            case INITIALIZE:
                protoBuf.writeDouble(this.centerX);
                protoBuf.writeDouble(this.centerZ);
                protoBuf.writeDouble(this.diameter);
                protoBuf.writeDouble(this.targetSize);
                protoBuf.writeVarLong(this.timeUntilTarget);
                protoBuf.writeVarInt(this.size);
                protoBuf.writeVarInt(this.warningDistance);
                protoBuf.writeVarInt(this.warningTime);
                break;

            default:
                break;
        }
    }

    public String toString() {
        return "PacketPlayServerWorldBorder(action=" + this.getAction()
                + ", size=" + this.getSize()
                + ", centerX=" + this.getCenterX()
                + ", centerZ=" + this.getCenterZ()
                + ", targetSize=" + this.getTargetSize()
                + ", diameter=" + this.getDiameter()
                + ", timeUntilTarget=" + this.getTimeUntilTarget()
                + ", warningTime=" + this.getWarningTime()
                + ", warningDistance=" + this.getWarningDistance()
                + ")";
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
