package com.github.phantompowered.proxy.protocol.play.server.player;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayPlayerCombatEvent implements Packet {

    private Event event;
    private int playerId; // event -> ENTITY_DIED
    private int entityId; // event -> ENTITY_DIED | END_COMBAT
    private int duration; // event -> END_COMBAT
    private String deathMessage;

    public PacketPlayPlayerCombatEvent(Event event, int playerId, int entityId, int duration, String deathMessage) {
        this.event = event;
        this.playerId = playerId;
        this.entityId = entityId;
        this.duration = duration;
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

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
            this.duration = protoBuf.readVarInt();
            this.entityId = protoBuf.readInt();
        } else if (this.event == Event.ENTITY_DIED) {
            this.playerId = protoBuf.readVarInt();
            this.entityId = protoBuf.readInt();
            this.deathMessage = protoBuf.readString();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.event.ordinal());

        if (this.event == Event.END_COMBAT) {
            protoBuf.writeVarInt(this.duration);
            protoBuf.writeInt(this.entityId);
        } else if (this.event == Event.ENTITY_DIED) {
            protoBuf.writeVarInt(this.playerId);
            protoBuf.writeInt(this.entityId);
            protoBuf.writeString(this.deathMessage);
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.COMBAT_EVENT;
    }

    public enum Event {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED,
        ;
    }

}
