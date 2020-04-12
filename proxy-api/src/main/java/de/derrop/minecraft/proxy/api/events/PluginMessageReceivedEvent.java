package de.derrop.minecraft.proxy.api.events;

import de.derrop.minecraft.proxy.api.connection.ProtocolDirection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.event.Cancelable;
import de.derrop.minecraft.proxy.api.events.util.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginMessageReceivedEvent extends PlayerEvent implements Cancelable {

    private boolean cancel = false;

    private String tag;
    private byte[] data;

    public PluginMessageReceivedEvent(@Nullable ProxiedPlayer player, @NotNull ProtocolDirection direction, String tag, byte[] data) {
        super(player, direction);
        this.tag = tag;
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
}
