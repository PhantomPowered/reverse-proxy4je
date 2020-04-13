package net.md_5.bungee.connection;

import de.derklaro.minecraft.proxy.connections.basic.BasicServiceConnection;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.chat.component.BaseComponent;
import de.derrop.minecraft.proxy.api.chat.component.TextComponent;
import de.derrop.minecraft.proxy.api.connection.Connection;
import de.derrop.minecraft.proxy.api.connection.ProtocolDirection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.events.connection.ChatEvent;
import de.derrop.minecraft.proxy.api.events.connection.PluginMessageEvent;
import de.derrop.minecraft.proxy.connection.cache.packet.system.Disconnect;
import de.derrop.minecraft.proxy.connection.cache.packet.system.JoinGame;
import net.md_5.bungee.Util;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.*;

public class DownstreamBridge extends PacketHandler {

    private BasicServiceConnection connection;
    private boolean disconnected = false;

    public DownstreamBridge(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private ProxiedPlayer con() {
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

    @Override
    public void handle(Disconnect disconnect) throws Exception {
        MCProxy.getInstance().unregisterConnection(this.connection);
        this.disconnectReceiver(disconnect.getReason());
    }

    private void disconnectReceiver(BaseComponent[] reason) {
        if (this.connection == null || this.disconnected) {
            return;
        }
        this.disconnected = true;

        System.out.println("Disconnected " + this.connection.getCredentials() + " (" + this.connection.getName() + "#" + this.connection.getUniqueId() + ") with " + TextComponent.toPlainText(reason));

        if (this.connection.getPlayer() != null) {
            ProxiedPlayer con = this.connection.getPlayer();
            this.connection.getClient().free();
            ((UserConnection) con).handleDisconnected(this.connection, reason);
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
        if (packet.packet instanceof JoinGame) {
            this.connection.getClient().setEntityId(((JoinGame) packet.packet).getEntityId());
        }
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
        this.connection.sendPacket(alive);
        this.connection.getClient().setLastAlivePacket(System.currentTimeMillis());
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Login login) throws Exception {
        this.connection.getClient().setEntityId(login.getEntityId());
        this.connection.getClient().connectionSuccess();
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(this.connection, ProtocolDirection.TO_CLIENT, pluginMessage.getTag(), pluginMessage.getData());
        if (this.connection.getProxy().getEventManager().callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());

        Connection connection = this.con();
        if (connection == null) {
            return;
        }

        connection.sendPacket(pluginMessage);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Kick kick) throws Exception {
        this.connection.getClient().setLastKickReason(ComponentSerializer.parse(kick.getMessage()));
        MCProxy.getInstance().unregisterConnection(this.connection);
        BasicServiceConnection proxyClient = MCProxy.getInstance().findBestConnection(null);
        if (proxyClient == null) {
            //con.disconnect0(ComponentSerializer.parse(kick.getMessage()));
            return;
        }
        this.connection = proxyClient;
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Chat chat) throws Exception {
        ChatEvent event = new ChatEvent(this.connection, ProtocolDirection.TO_CLIENT, ComponentSerializer.parse(chat.getMessage()));
        if (this.connection.getProxy().getEventManager().callEvent(event).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }
        chat.setMessage(ComponentSerializer.toString(event.getMessage()));
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
    }

    @Override
    public void handle(TabCompleteResponse tabCompleteResponse) throws Exception {
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
    public void handle(Respawn respawn) {
        this.connection.getClient().setDimension(respawn.getDimension());
    }

    @Override
    public void handle(Commands commands) throws Exception {
        /*boolean modified = false;

        for ( Map.Entry<String, Command> command : bungee.getPluginManager().getCommands() )
        {
            if ( !bungee.getDisabledCommands().contains( command.getKey() ) && commands.getRoot().getChild( command.getKey() ) == null && command.getValue().hasPermission( con ) )
            {
                LiteralCommandNode dummy = LiteralArgumentBuilder.literal( command.getKey() )
                        .then( RequiredArgumentBuilder.argument( "args", StringArgumentType.greedyString() )
                                .suggests( Commands.SuggestionRegistry.ASK_SERVER ) )
                        .build();
                commands.getRoot().addChild( dummy );

                modified = true;
            }
        }

        if ( modified )
        {
            con.sendPacket( commands );
            throw CancelSendSignal.INSTANCE;
        }*/
    }

    @Override
    public String toString() {
        if (this.connection == null) {
            return "[] <-> DownstreamBridge <-> []";
        }
        return "[" + this.connection.getName() + "] <-> DownstreamBridge <-> [" + this.connection.getCredentials().getEmail() + "]";
    }
}
