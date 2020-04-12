package net.md_5.bungee.connection;

import de.derklaro.minecraft.proxy.TheProxy;
import de.derklaro.minecraft.proxy.events.PluginMessageReceivedEvent;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.cache.packet.system.Disconnect;
import de.derrop.minecraft.proxy.connection.cache.packet.system.JoinGame;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.*;

public class DownstreamBridge extends PacketHandler {

    private ConnectedProxyClient proxyClient;
    private boolean disconnected = false;

    public DownstreamBridge(ConnectedProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    private UserConnection con() {
        return this.proxyClient != null ? this.proxyClient.getRedirector() : null;
    }

    @Override
    public void exception(Throwable t) throws Exception {
        System.err.println("Exception on proxy client " + this.proxyClient.getAccountName() + "!");
        t.printStackTrace();
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        this.disconnectReceiver(TextComponent.fromLegacyText("§c" + Util.exception(t)));
        proxyClient.getChannelWrapper().close();
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        // We lost connection to the server
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        this.disconnectReceiver(TextComponent.fromLegacyText("§cNo reason given"));
    }

    @Override
    public void handle(Disconnect disconnect) throws Exception {
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        this.disconnectReceiver(disconnect.getReason());
    }

    private void disconnectReceiver(BaseComponent[] reason) {
        if (this.proxyClient == null || this.disconnected) {
            return;
        }
        this.disconnected = true;

        if (this.proxyClient.getCredentials() != null) {
            System.out.println("Disconnected " + this.proxyClient.getCredentials() + " (" + this.proxyClient.getAccountName() + "#" + this.proxyClient.getAccountUUID() + ") with " + TextComponent.toPlainText(reason));
        } else {
            System.out.println("Disconnected with " + TextComponent.toPlainText(reason));
        }
        if (this.proxyClient.getRedirector() != null) {
            UserConnection con = this.proxyClient.getRedirector();
            this.proxyClient.free();
            con.handleDisconnected(this.proxyClient, reason);
        }
        this.proxyClient.setLastKickReason(reason);
        this.proxyClient.connectionFailed();
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return true;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        this.proxyClient.getEntityMap().rewriteClientbound(packet.buf, this.proxyClient.getEntityId(), this.proxyClient.getEntityId(), 47);
        this.proxyClient.redirectPacket(packet.buf, packet.packet);
        if (packet.packet instanceof JoinGame) {
            this.proxyClient.setEntityId(((JoinGame) packet.packet).getEntityId());
        }
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
        this.proxyClient.getChannelWrapper().write(alive);
        this.proxyClient.setLastAlivePacket(System.currentTimeMillis());
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Login login) throws Exception {
        this.proxyClient.setEntityId(login.getEntityId());
        this.proxyClient.connectionSuccess();
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (TheProxy.getTheProxy().getEventManager().callEvent(new PluginMessageReceivedEvent(this.con(), ProtocolConstants.Direction.TO_CLIENT, pluginMessage)).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        Connection connection = this.con();
        if (connection == null) {
            return;
        }

        connection.unsafe().sendPacket(pluginMessage);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Kick kick) throws Exception {
        this.proxyClient.setLastKickReason(ComponentSerializer.parse(kick.getMessage()));
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        ConnectedProxyClient proxyClient = MCProxy.getInstance().findBestProxyClient(null);
        if (proxyClient == null) {
            //con.disconnect0(ComponentSerializer.parse(kick.getMessage()));
            return;
        }
        this.proxyClient = proxyClient;
        throw CancelSendSignal.INSTANCE;
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

            con.unsafe().sendPacket( tabCompleteResponse );
        }

        throw CancelSendSignal.INSTANCE;*/
    }

    @Override
    public void handle(BossBar bossBar) {
        if (this.con() == null) {
            return;
        }
        switch (bossBar.getAction()) {
            // Handle add bossbar
            case 0:
                this.con().getSentBossBars().add(bossBar.getUuid());
                break;
            // Handle remove bossbar
            case 1:
                this.con().getSentBossBars().remove(bossBar.getUuid());
                break;
        }
    }

    @Override
    public void handle(Respawn respawn) {
        this.proxyClient.setDimension(respawn.getDimension());
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
            con.unsafe().sendPacket( commands );
            throw CancelSendSignal.INSTANCE;
        }*/
    }

    @Override
    public String toString() {
        if (this.proxyClient == null) {
            return "[] <-> DownstreamBridge <-> []";
        }
        return "[" + this.proxyClient.getAccountName() + "] <-> DownstreamBridge <-> [" + this.proxyClient.getCredentials().getEmail() + "]";
    }
}
