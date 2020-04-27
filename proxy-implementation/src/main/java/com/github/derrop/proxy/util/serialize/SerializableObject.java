package com.github.derrop.proxy.util.serialize;

import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.util.Rotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Minecrafts DataWatcher.WatchableObject
 */
public class SerializableObject {

    public static final int END = 127;

    private static final Map<Class<?>, Integer> DATA_TYPES = new HashMap<>();

    static {
        DATA_TYPES.put(Byte.class, 0);
        DATA_TYPES.put(Short.class, 1);
        DATA_TYPES.put(Integer.class, 2);
        DATA_TYPES.put(Float.class, 3);
        DATA_TYPES.put(String.class, 4);
        DATA_TYPES.put(ItemStack.class, 5);
        DATA_TYPES.put(BlockPos.class, 6);
        DATA_TYPES.put(Rotations.class, 7);
    }

    public static int getDataType(Class<?> type) {
        return DATA_TYPES.getOrDefault(type, -1);
    }

    private final int objectType;
    private final int id;
    private Object value;

    public SerializableObject(int type, int id, Object value) {
        this.id = id;
        this.value = value;
        this.objectType = type;
    }

    public int getId() {
        return this.id;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Optional<Object> getValue() {
        return Optional.ofNullable(this.value);
    }

    public int getObjectType() {
        return this.objectType;
    }

    public void write(ProtoBuf buffer) {
        int i = (this.objectType << 5 | this.id & 31) & 255;
        buffer.writeByte(i);

        switch (this.objectType) {
            case 0:
                buffer.writeByte((byte) this.value);
                break;

            case 1:
                buffer.writeShort((short) this.value);
                break;

            case 2:
                buffer.writeInt((int) this.value);
                break;

            case 3:
                buffer.writeFloat((float) this.value);
                break;

            case 4:
                buffer.writeString((String) this.value);
                break;

            case 5:
                buffer.writeItemStack((ItemStack) this.value);
                break;

            case 6:
                BlockPos pos = (BlockPos) this.value;
                buffer.writeInt(pos.getX());
                buffer.writeInt(pos.getY());
                buffer.writeInt(pos.getZ());
                break;

            case 7:
                Rotations rotations = (Rotations) this.value;
                buffer.writeFloat(rotations.getX());
                buffer.writeFloat(rotations.getY());
                buffer.writeFloat(rotations.getZ());
        }
    }

    public static SerializableObject read(ProtoBuf buffer) {
        byte in = buffer.readByte();
        if (in == END) {
            return null;
        }

        int typeId = (in & 224) >> 5;
        int id = in & 31;

        Object value = null;

        switch (typeId) {
            case 0:
                value = buffer.readByte();
                break;

            case 1:
                value = buffer.readShort();
                break;

            case 2:
                value = buffer.readInt();
                break;

            case 3:
                value = buffer.readFloat();
                break;

            case 4:
                value = buffer.readString();
                break;

            case 5:
                value = buffer.readItemStack();
                break;

            case 6: {
                int x = buffer.readInt();
                int y = buffer.readInt();
                int z = buffer.readInt();
                value = new BlockPos(x, y, z);
                break;
            }

            case 7: {
                float x = buffer.readFloat();
                float y = buffer.readFloat();
                float z = buffer.readFloat();
                value = new Rotations(x, y, z);
                break;
            }
        }

        return value != null ? new SerializableObject(typeId, id, value) : null;
    }

}