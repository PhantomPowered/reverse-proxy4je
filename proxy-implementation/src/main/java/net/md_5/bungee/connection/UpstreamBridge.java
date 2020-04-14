package net.md_5.bungee.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerLogoutEvent;
import com.github.derrop.proxy.entity.player.DefaultPlayer;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientSettings;
import com.github.derrop.proxy.protocol.play.client.PacketPlayClientTabCompleteRequest;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayChatMessage;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
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
    public void handle(PacketPlayKeepAlive alive) throws Exception {
    }

    @Override
    public void handle(PacketPlayChatMessage chat) throws Exception {
        int maxLength = (con.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_11) ? 256 : 100;
        if (chat.getMessage().length() >= maxLength) {
            throw CancelSendSignal.INSTANCE;
        }

        if (chat.getMessage().startsWith("/proxy ")) {
            try {
                CommandMap commandMap = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(CommandMap.class);
                if (commandMap.process(this.con, chat.getMessage().replaceFirst("/proxy ", "")) != CommandResult.NOT_FOUND) {
                    throw CancelSendSignal.INSTANCE;
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                this.con.sendMessage("Unable to process command: " + ex.getMessage());
                throw CancelSendSignal.INSTANCE;
            }

            return;
        }

        ChatEvent event = new ChatEvent(this.con, ProtocolDirection.TO_CLIENT, TextComponent.fromLegacyText(chat.getMessage()));
        if (this.con.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    @Override
    public void handle(PacketPlayClientTabCompleteRequest tabComplete) throws Exception {
        /*List<String> suggestions = new ArrayList<>();

        if ( tabComplete.getCursor().startsWith( "/" ) )
        {
            bungee.getPluginManager().dispatchCommand( con, tabComplete.getCursor().substring( 1 ), suggestions );
        }

        TabCompleteEvent tabCompleteEvent = new TabCompleteEvent( con, con.getServer(), tabComplete.getCursor(), suggestions );
        bungee.getPluginManager().callEvent( tabCompleteEvent );

        if ( tabCompleteEvent.isCancelled() )
        {
            throw CancelSendSignal.INSTANCE;
        }

        List<String> results = tabCompleteEvent.getSuggestions();
        if ( !results.isEmpty() )
        {
            // Unclear how to handle 1.13 commands at this point. Because we don't inject into the command packets we are unlikely to get this far unless
            // Bungee plugins are adding results for commands they don't own anyway
            if ( con.getPendingConnection().getVersion() < ProtocolConstants.MINECRAFT_1_13 )
            {
                con.sendPacket( new TabCompleteResponse( results ) );
            } else
            {
                int start = tabComplete.getCursor().lastIndexOf( ' ' ) + 1;
                int end = tabComplete.getCursor().length();
                StringRange range = StringRange.between( start, end );

                List<Suggestion> brigadier = new LinkedList<>();
                for ( String s : results )
                {
                    brigadier.add( new Suggestion( range, s ) );
                }

                con.sendPacket( new TabCompleteResponse( tabComplete.getTransactionId(), new Suggestions( range, brigadier ) ) );
            }
            throw CancelSendSignal.INSTANCE;
        }*/
    }

    @Override
    public void handle(PacketPlayClientSettings settings) throws Exception {
    }

    @Override
    public void handle(PacketPlayPluginMessage pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(this.con, ProtocolDirection.TO_SERVER, pluginMessage.getTag(), pluginMessage.getData());
        if (this.con.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());

        if (PacketPlayPluginMessage.SHOULD_RELAY.apply(pluginMessage)) {
            this.con.getPendingConnection().getRelayMessages().add(pluginMessage);
        }
    }

    @Override
    public String toString() {
        return "[" + con.getName() + "] -> UpstreamBridge";
    }
}
