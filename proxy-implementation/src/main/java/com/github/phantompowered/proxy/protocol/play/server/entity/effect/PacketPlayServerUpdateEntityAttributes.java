package com.github.phantompowered.proxy.protocol.play.server.entity.effect;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class PacketPlayServerUpdateEntityAttributes implements Packet {

    private int entityId;
    private Collection<Attribute> attributes;

    public PacketPlayServerUpdateEntityAttributes(int entityId, Collection<Attribute> attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    public PacketPlayServerUpdateEntityAttributes() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        int size = protoBuf.readInt();

        this.attributes = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String name = protoBuf.readString();
            double value = protoBuf.readDouble();
            int modifierCount = protoBuf.readVarInt();
            Collection<AttributeModifier> modifiers = new ArrayList<>(modifierCount);

            for (int j = 0; j < modifierCount; j++) {
                modifiers.add(new AttributeModifier(protoBuf.readUniqueId(), "Unknown synced attribute modifier", protoBuf.readDouble(), protoBuf.readByte()));
            }

            this.attributes.add(new Attribute(name, value, modifiers));
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeInt(this.attributes.size());

        for (Attribute attribute : this.attributes) {
            protoBuf.writeString(attribute.getName());
            protoBuf.writeDouble(attribute.getValue());

            protoBuf.writeVarInt(attribute.getModifiers().size());
            for (AttributeModifier modifier : attribute.getModifiers()) {
                protoBuf.writeUniqueId(modifier.getId());
                protoBuf.writeDouble(modifier.getAmount());
                protoBuf.writeByte(modifier.getOperation());
            }
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_ATTRIBUTES;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "PacketPlayServerUpdateEntityAttributes{"
                + "entityId=" + entityId
                + ", attributes=" + attributes
                + '}';
    }

    public class Attribute {
        private String name;
        private double value;
        private Collection<AttributeModifier> modifiers;

        public Attribute(String name, double value, Collection<AttributeModifier> modifiers) {
            this.name = name;
            this.value = value;
            this.modifiers = modifiers;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public Collection<AttributeModifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Collection<AttributeModifier> modifiers) {
            this.modifiers = modifiers;
        }

        @Override
        public String toString() {
            return "Attribute{"
                    + "name='" + name + '\''
                    + ", value=" + value
                    + ", modifiers=" + modifiers
                    + '}';
        }
    }

    public class AttributeModifier {
        private double amount;
        private int operation;
        private String name;
        private UUID id;

        public AttributeModifier(UUID id, String name, double amount, int operation) {
            this.amount = amount;
            this.operation = operation;
            this.name = name;
            this.id = id;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getOperation() {
            return operation;
        }

        public void setOperation(int operation) {
            this.operation = operation;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "AttributeModifier{"
                    + "amount=" + amount
                    + ", operation=" + operation
                    + ", name='" + name + '\''
                    + ", id=" + id
                    + '}';
        }
    }

}
