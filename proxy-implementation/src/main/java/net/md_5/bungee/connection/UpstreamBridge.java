package net.md_5.bungee.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerLogoutEvent;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.entity.player.DefaultPlayer;
import com.github.derrop.proxy.protocol.play.client.PacketPlayChatMessage;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import net.md_5.bungee.Util;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

public class UpstreamBridge extends PacketHandler {

    private final DefaultPlayer con;

    public UpstreamBridge(@NotNull DefaultPlayer con) {
        this.con = con;
    }

    @Override
    public void exception(Throwable t) {
        t.printStackTrace();
        con.sendMessage("Unexpected exception, check log for more details: " + Util.exception(t));
    }

    @Override
    public void disconnected(ChannelWrapper channel) {
        this.con.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerLogoutEvent(this.con));
        this.con.setConnected(false);
        if (this.con.getConnectedClient() != null) {
            this.con.getConnectedClient().getClient().free();
        }
    }

    @Override
    public void writabilityChanged(ChannelWrapper channel) {
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) {
        return packet.id != 27; // disconnect packet from the client, the proxy should stay connected
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (con.getConnectedClient() != null && con.getConnectedClient().isConnected()) {
            con.getConnectedClient().getClient().handleClientPacket(packet);

            con.getEntityMap().rewriteServerbound(packet.buf, con.getEntityId(), con.getEntityId(), con.getPendingConnection().getVersion());
            if (packet.packet != null) {
                con.getConnectedClient().getClient().getVelocityHandler().handlePacket(ProtocolConstants.Direction.TO_SERVER, packet.packet);
            }

            con.getConnectedClient().sendPacket(packet);
        }
    }

    @Override
    public void handle(@NotNull Packet packet) throws Exception {
        if (packet instanceof PacketPlayChatMessage) {
            this.handleChat((PacketPlayChatMessage) packet);
            return;
        }

        if (packet instanceof PacketPlayPluginMessage) {
            this.handlePluginMessage((PacketPlayPluginMessage) packet);
        }
    }

    private void handleChat(PacketPlayChatMessage chat) throws Exception {
        int maxLength = (con.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_11) ? 256 : 100;
        if (chat.getMessage().length() >= maxLength) {
            throw CancelProceedException.INSTANCE;
        }

        if (chat.getMessage().startsWith("/proxy ")) {
            try {
                CommandMap commandMap = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(CommandMap.class);
                if (commandMap.process(this.con, chat.getMessage().replaceFirst("/proxy ", "")) != CommandResult.NOT_FOUND) {
                    throw CancelProceedException.INSTANCE;
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                this.con.sendMessage("Unable to process command: " + ex.getMessage());
                throw CancelProceedException.INSTANCE;
            }

            return;
        }

        ChatEvent event = new ChatEvent(this.con, ProtocolDirection.TO_CLIENT, TextComponent.fromLegacyText(chat.getMessage()));
        if (this.con.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    private void handlePluginMessage(PacketPlayPluginMessage pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(this.con, ProtocolDirection.TO_SERVER, pluginMessage.getTag(), pluginMessage.getData());
        if (this.con.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());
    }

    @Override
    public String toString() {
        return "[" + con.getName() + "] -> UpstreamBridge";
    }
}
