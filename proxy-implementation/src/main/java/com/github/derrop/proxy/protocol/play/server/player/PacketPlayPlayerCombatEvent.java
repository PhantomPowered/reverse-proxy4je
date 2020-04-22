package com.github.derrop.proxy.protocol.play.server.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayPlayerCombatEvent implements Packet {

    private Event event;
    // TODO what are these fields for?
    private int a;
    private int b;
    private int c;
    private String deathMessage;

    public PacketPlayPlayerCombatEvent(Event event, int a, int b, int c, String deathMessage) {
        this.event = event;
        this.a = a;
        this.b = b;
        this.c = c;
        this.deathMessage = deathMessage;
    }

    public PacketPlayPlayerCombatEvent() {
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.event = Event.values()[protoBuf.readVarInt()];

        if (this.event == Event.END_COMBAT) {
            this.c = protoBuf.readVarInt();
            this.b = protoBuf.readInt();
        } else if (this.event == Event.ENTITY_DIED) {
            this.a = protoBuf.readVarInt();
            this.b = protoBuf.readInt();
            this.deathMessage = protoBuf.readString();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.event.ordinal());

        if (this.event == Event.END_COMBAT) {
            protoBuf.writeVarInt(this.c);
            protoBuf.writeInt(this.b);
        } else if (this.event == Event.ENTITY_DIED) {
            protoBuf.writeVarInt(this.a);
            protoBuf.writeInt(this.b);
            protoBuf.writeString(this.deathMessage);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.COMBAT_EVENT;
    }

    public static enum Event {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED,
        ;
    }

}
