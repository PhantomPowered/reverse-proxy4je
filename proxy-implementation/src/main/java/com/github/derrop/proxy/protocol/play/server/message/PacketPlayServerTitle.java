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
package com.github.derrop.proxy.protocol.play.server.message;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerTitle implements Packet {

    private Action action;
    private String text;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public PacketPlayServerTitle() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TITLE;
    }

    public Action getAction() {
        return this.action;
    }

    public String getText() {
        return this.text;
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = protoBuf.readVarInt();
        if (index >= 2) {
            index++;
        }

        this.action = Action.values()[index];
        switch (action) {
            case TITLE:
            case SUBTITLE:
            case PLACEHOLDER:
                this.text = protoBuf.readString();
                break;

            case TIMES:
                this.fadeIn = protoBuf.readInt();
                this.stay = protoBuf.readInt();
                this.fadeOut = protoBuf.readInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = this.action.ordinal();
        if (index >= 2) {
            index--;
        }

        protoBuf.writeVarInt(index);
        switch (action) {
            case TITLE:
            case SUBTITLE:
            case PLACEHOLDER:
                protoBuf.writeString(this.text);
                break;

            case TIMES:
                protoBuf.writeInt(this.fadeIn);
                protoBuf.writeInt(this.stay);
                protoBuf.writeInt(this.fadeOut);
                break;
        }
    }

    public String toString() {
        return "PacketPlayServerTitle(action=" + this.getAction() + ", text=" + this.getText() + ", fadeIn=" + this.getFadeIn() + ", stay=" + this.getStay() + ", fadeOut=" + this.getFadeOut() + ")";
    }

    public enum Action {

        TITLE,
        SUBTITLE,
        PLACEHOLDER,
        TIMES,
        CLEAR,
        RESET
    }
}
