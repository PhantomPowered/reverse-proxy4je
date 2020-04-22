package com.github.derrop.proxy.protocol.play.server.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PacketPlayServerPlayerStatistics implements Packet {

    private Map<String, Integer> statistics;

    public PacketPlayServerPlayerStatistics(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }

    public PacketPlayServerPlayerStatistics() {
    }

    public Map<String, Integer> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int count = protoBuf.readVarInt();
        this.statistics = new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            this.statistics.put(protoBuf.readString(), protoBuf.readVarInt());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.statistics.size());

        for (Map.Entry<String, Integer> entry : this.statistics.entrySet()) {
            protoBuf.writeString(entry.getKey());
            protoBuf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.STATISTIC;
    }
}
