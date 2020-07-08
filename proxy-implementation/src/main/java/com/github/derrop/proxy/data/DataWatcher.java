package com.github.derrop.proxy.data;

import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.EulerAngle;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataWatcher {

    public DataWatcher() {
    }

    private final Map<Integer, DataWatcherEntry> objects = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Collection<DataWatcherEntry> getObjects() {
        return Collections.unmodifiableCollection(this.objects.values());
    }

    public <T> void addObject(int id, T object) {
        int typeId = DataWatcherEntry.getDataType(object.getClass());

        if (typeId == -1) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        } else if (id > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        } else if (this.objects.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        } else {
            DataWatcherEntry dataWatcherEntry = new DataWatcherEntry(typeId, id, object);
            this.lock.writeLock().lock();
            this.objects.put(id, dataWatcherEntry);
            this.lock.writeLock().unlock();
        }
    }

    public void updateObject(int id, Object value) {
        Optional<DataWatcherEntry> optional = this.getObject(id);
        if (optional.isPresent()) {
            optional.get().setValue(value);
        } else {
            this.addObject(id, value);
        }
    }

    public void addEmptyObjectByDataType(int id, int type) {
        DataWatcherEntry dataWatcherEntry = new DataWatcherEntry(type, id, null);
        this.lock.writeLock().lock();
        this.objects.put(id, dataWatcherEntry);
        this.lock.writeLock().unlock();
    }

    public byte getByte(int id) {
        Object val = this.getObjectValue(id).orElse(-1);
        return val instanceof Number ? ((Number) val).byteValue() : -1;
    }

    public void updateByte(int id, int value) {
        this.updateObject(id, (byte) value);
    }

    public boolean getBoolean(int id) {
        return this.getByte(id) == 1;
    }

    public void updateBoolean(int id, boolean value) {
        this.updateByte(id, value ? 1 : 0);
    }

    public short getShort(int id) {
        Object val = this.getObjectValue(id).orElse(-1);
        return val instanceof Number ? ((Number) val).shortValue() : -1;
    }

    public void updateShort(int id, int value) {
        this.updateObject(id, (short) value);
    }

    public int getInt(int id) {
        Object val = this.getObjectValue(id).orElse(-1);
        return val instanceof Number ? ((Number) val).intValue() : -1;
    }

    public void updateInt(int id, int value) {
        this.updateObject(id, value);
    }

    public float getFloat(int id) {
        Object val = this.getObjectValue(id).orElse(-1);
        return val instanceof Number ? ((Number) val).floatValue() : -1;
    }

    public void updateFloat(int id, double value) {
        this.updateObject(id, (float) value);
    }

    public String getString(int id) {
        return (String) this.getObjectValue(id).orElse(null);
    }

    public void updateString(int id, String value) {
        this.updateObject(id, value);
    }

    public ItemStack getItemStack(int id) {
        return (ItemStack) this.getObjectValue(id).orElse(null);
    }

    public void updateItemStack(int id, ItemStack itemStack) {
        this.updateObject(id, itemStack);
    }

    public EulerAngle getEulerAngle(int id) {
        return (EulerAngle) this.getObjectValue(id).orElse(null);
    }

    public void updateEulerAngle(int id, EulerAngle angle) {
        this.updateObject(id, angle);
    }

    private Optional<DataWatcherEntry> getObject(int id) {
        this.lock.readLock().lock();
        DataWatcherEntry dataWatcherEntry;

        try {
            dataWatcherEntry = this.objects.get(id);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }

        this.lock.readLock().unlock();
        return Optional.ofNullable(dataWatcherEntry);
    }

    private Optional<Object> getObjectValue(int id) {
        return this.getObject(id).flatMap(DataWatcherEntry::getValue);
    }

    public void applyUpdate(Collection<DataWatcherEntry> objects) {
        this.lock.writeLock().lock();

        for (DataWatcherEntry object : objects) {
            DataWatcherEntry previous = this.objects.get(object.getId());
            if (previous != null) {
                previous.setValue(object.getValue().orElse(null));
            } else {
                this.objects.put(object.getId(), object);
            }
        }

        this.lock.writeLock().unlock();
    }

    public static void writeList(ProtoBuf buffer, Collection<DataWatcherEntry> objects) {
        if (objects != null) {
            for (DataWatcherEntry object : objects) {
                object.write(buffer);
            }
        }
        buffer.writeByte(DataWatcherEntry.END);
    }

    public static Collection<DataWatcherEntry> readList(ProtoBuf buffer) {
        Collection<DataWatcherEntry> objects = new ArrayList<>();
        DataWatcherEntry object;
        while ((object = DataWatcherEntry.read(buffer)) != null) {
            objects.add(object);
        }

        return objects;
    }

}
