package de.derklaro.minecraft.proxy.events;

import de.derklaro.minecraft.proxy.event.Cancelable;
import de.derklaro.minecraft.proxy.events.util.ConnectionEvent;
import net.md_5.bungee.connection.Connection;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginMessageReceivedEvent extends ConnectionEvent implements Cancelable {

    private boolean cancel = false;

    private final PluginMessage pluginMessage;

    public PluginMessageReceivedEvent(@Nullable Connection connection, @NotNull ProtocolConstants.Direction direction, @NotNull PluginMessage pluginMessage) {
        super(connection, direction);
        this.pluginMessage = pluginMessage;
    }

    public PluginMessage getPluginMessage() {
        return pluginMessage;
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
