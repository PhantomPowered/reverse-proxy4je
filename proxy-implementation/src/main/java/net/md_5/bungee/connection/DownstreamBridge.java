package net.md_5.bungee.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.protocol.login.PacketLoginSetCompression;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.derrop.proxy.protocol.play.client.PacketPlayChatMessage;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayPluginMessage;
import net.md_5.bungee.Util;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;

public class DownstreamBridge extends PacketHandler {

    private BasicServiceConnection connection;
    private boolean disconnected = false;

    public DownstreamBridge(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private Player con() {
        return this.connection != null ? this.connection.getPlayer() : null;
    }

    @Override
    public void exception(Throwable t) throws Exception {
        System.err.println("Exception on proxy client " + this.connection.getName() + "!");
        t.printStackTrace();
        MCProxy.getInstance().unregisterConnection(this.connection);
        this.disconnectReceiver(TextComponent.fromLegacyText("§c" + Util.exception(t)));
        connection.close();
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        // We lost connection to the server
        MCProxy.getInstance().unregisterConnection(this.connection);
        this.disconnectReceiver(TextComponent.fromLegacyText("§cNo reason given"));
    }

    private void disconnectReceiver(BaseComponent[] reason) {
        if (this.connection == null || this.disconnected) {
            return;
        }
        this.disconnected = true;

        System.out.println("Disconnected " + this.connection.getCredentials() + " (" + this.connection.getName() + "#" + this.connection.getUniqueId() + ") with " + TextComponent.toPlainText(reason));

        if (this.connection.getPlayer() != null) {
            Player con = this.connection.getPlayer();
            this.connection.getClient().free();
            con.handleDisconnected(this.connection, reason);
        }

        this.connection.getClient().setLastKickReason(reason);
        this.connection.getClient().connectionFailed();
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return true;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        this.connection.getClient().getEntityMap().rewriteClientbound(packet.buf, this.connection.getEntityId(), this.connection.getEntityId(), 47);
        this.connection.getClient().redirectPacket(packet.buf, packet.packet);
        /*if (packet.packet instanceof JoinGame) {
            this.connection.getClient().setEntityId(((JoinGame) packet.packet).getEntityId());
        }*/
    }

    @Override
    public void handle(PacketPlayKeepAlive alive) throws Exception {
        this.connection.sendPacket(alive);
        this.connection.getClient().setLastAlivePacket(System.currentTimeMillis());
        throw CancelProceedException.INSTANCE;
    }

    @Override
    public void handle(PacketPlayServerLogin login) throws Exception {
        this.connection.getClient().setEntityId(login.getEntityId());
        this.connection.getClient().connectionSuccess();
    }

    @Override
    public void handle(PacketPlayPluginMessage pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(this.connection, ProtocolDirection.TO_CLIENT, pluginMessage.getTag(), pluginMessage.getData());
        if (this.connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());

        Connection connection = this.con();
        if (connection == null) {
            return;
        }

        connection.sendPacket(pluginMessage);
        throw CancelProceedException.INSTANCE;
    }

    @Override
    public void handle(PacketPlayServerKickPlayer kick) throws Exception {
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        this.disconnectReceiver(reason);

        this.connection.getClient().setLastKickReason(reason);
        MCProxy.getInstance().unregisterConnection(this.connection);
        BasicServiceConnection proxyClient = MCProxy.getInstance().findBestConnection(null);
        if (proxyClient == null) {
            //con.disconnect0(ComponentSerializer.parse(kick.getMessage()));
            return;
        }
        this.connection = proxyClient;
        throw CancelProceedException.INSTANCE;
    }

    @Override
    public void handle(PacketPlayChatMessage chat) throws Exception {
        ChatEvent event = new ChatEvent(this.connection, ProtocolDirection.TO_CLIENT, ComponentSerializer.parse(chat.getMessage()));
        if (this.connection.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    @Override
    public void handle(PacketLoginSetCompression setCompression) throws Exception {
    }

    @Override
    public void handle(PacketPlayServerTabCompleteResponse tabCompleteResponse) throws Exception {
        /*List<String> commands = tabCompleteResponse.getCommands();
        if ( commands == null )
        {
            commands = Lists.transform( tabCompleteResponse.getSuggestions().getList(), new Function<Suggestion, String>()
            {
                @Override
                public String apply(Suggestion input)
                {
                    return input.getText();
                }
            } );
        }

        TabCompleteResponseEvent tabCompleteResponseEvent = new TabCompleteResponseEvent( server, con, new ArrayList<>( commands ) );
        if ( !bungee.getPluginManager().callEvent( tabCompleteResponseEvent ).isCancelled() )
        {
            // Take action only if modified
            if ( !commands.equals( tabCompleteResponseEvent.getSuggestions() ) )
            {
                if ( tabCompleteResponse.getCommands() != null )
                {
                    // Classic style
                    tabCompleteResponse.setCommands( tabCompleteResponseEvent.getSuggestions() );
                } else
                {
                    // Brigadier style
                    final StringRange range = tabCompleteResponse.getSuggestions().getRange();
                    tabCompleteResponse.setSuggestions( new Suggestions( range, Lists.transform( tabCompleteResponseEvent.getSuggestions(), new Function<String, Suggestion>()
                    {
                        @Override
                        public Suggestion apply(String input)
                        {
                            return new Suggestion( range, input );
                        }
                    } ) ) );
                }
            }

            con.sendPacket( tabCompleteResponse );
        }

        throw CancelSendSignal.INSTANCE;*/
    }

    @Override
    public void handle(PacketPlayServerRespawn respawn) {
        this.connection.getClient().setDimension(respawn.getDimension());
    }

    @Override
    public String toString() {
        if (this.connection == null) {
            return "[] <-> DownstreamBridge <-> []";
        }
        return "[" + this.connection.getName() + "] <-> DownstreamBridge <-> [" + this.connection.getCredentials().getEmail() + "]";
    }
}
