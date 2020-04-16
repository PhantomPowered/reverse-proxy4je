package com.github.derrop.proxy.protocol.play.server;

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
