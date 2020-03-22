package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import de.derrop.minecraft.proxy.MCProxy;
import net.md_5.bungee.Util;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.*;

public class UpstreamBridge extends PacketHandler {

    private final UserConnection con;

    public UpstreamBridge(UserConnection con) {
        this.con = con;

        con.getTabListHandler().onConnect();
    }

    @Override
    public void exception(Throwable t) throws Exception {
        t.printStackTrace();
        con.disconnect(Util.exception(t));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        // We lost connection to the client
        con.getTabListHandler().onDisconnect();
    }

    @Override
    public void writabilityChanged(ChannelWrapper channel) throws Exception {
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return packet.id != 27; // disconnect packet from the client, the proxy should stay connected
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (con.getProxyClient() != null) {
            con.getEntityRewrite().rewriteServerbound(packet.buf, con.getClientEntityId(), con.getServerEntityId(), con.getPendingConnection().getVersion());
            if (packet.packet != null) {
                con.getProxyClient().getVelocityHandler().handlePacket(ProtocolConstants.Direction.TO_SERVER, packet.packet);
            }
            con.getProxyClient().getChannelWrapper().write(packet);
        }
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
    }

    @Override
    public void handle(Chat chat) throws Exception {
        int maxLength = (con.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_11) ? 256 : 100;
        Preconditions.checkArgument(chat.getMessage().length() <= maxLength, "Chat message too long"); // Mojang limit, check on updates

        if (MCProxy.getInstance().getCommandMap().dispatchCommand(con, chat.getMessage())) {
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(TabCompleteRequest tabComplete) throws Exception {
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
                con.unsafe().sendPacket( new TabCompleteResponse( results ) );
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

                con.unsafe().sendPacket( new TabCompleteResponse( tabComplete.getTransactionId(), new Suggestions( range, brigadier ) ) );
            }
            throw CancelSendSignal.INSTANCE;
        }*/
    }

    @Override
    public void handle(ClientSettings settings) throws Exception {
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (pluginMessage.getTag().equals("BungeeCord")) {
            throw CancelSendSignal.INSTANCE;
        }

        if (PluginMessage.SHOULD_RELAY.apply(pluginMessage)) {
            con.getPendingConnection().getRelayMessages().add(pluginMessage);
        }
    }

    @Override
    public String toString() {
        return "[" + con.getName() + "] -> UpstreamBridge";
    }
}
