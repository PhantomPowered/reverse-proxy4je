package net.md_5.bungee.connection;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.cache.packet.Disconect;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.api.score.*;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.*;

@AllArgsConstructor
public class DownstreamBridge extends PacketHandler {

    private ConnectedProxyClient proxyClient;

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
    public void handle(Disconect disconect) throws Exception {
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        this.disconnectReceiver(disconect.getReason());
    }

    private void disconnectReceiver(BaseComponent[] reason) {
        if (this.proxyClient.getRedirector() != null) {
            UserConnection con = this.proxyClient.getRedirector();
            this.proxyClient.free();
            con.handleDisconnected(this.proxyClient, reason);
        }
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return true;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        this.proxyClient.getEntityMap().rewriteClientbound(packet.buf, this.proxyClient.getEntityId(), this.proxyClient.getEntityId(), 47);
        this.proxyClient.redirectPacket(packet.buf, packet.packet);
        //this.proxyClient.redirectPacket(packet.buf);
    }

    @Override
    public void handle(KeepAlive alive) throws Exception {
        this.proxyClient.getChannelWrapper().write(alive);
        this.proxyClient.setLastAlivePacket(System.currentTimeMillis());
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(PlayerListItem playerList) throws Exception {
    }

    @Override
    public void handle(ScoreboardObjective objective) throws Exception {
        Scoreboard serverScoreboard = this.proxyClient.getScoreboard();
        switch (objective.getAction()) {
            case 0:
                serverScoreboard.addObjective(new Objective(objective.getName(), objective.getValue(), objective.getType().toString()));
                break;
            case 1:
                serverScoreboard.removeObjective(objective.getName());
                break;
            case 2:
                Objective oldObjective = serverScoreboard.getObjective(objective.getName());
                if (oldObjective != null) {
                    oldObjective.setValue(objective.getValue());
                    oldObjective.setType(objective.getType().toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown objective action: " + objective.getAction());
        }
    }

    @Override
    public void handle(ScoreboardScore score) throws Exception {
        Scoreboard serverScoreboard = this.proxyClient.getScoreboard();
        switch (score.getAction()) {
            case 0:
                Score s = new Score(score.getItemName(), score.getScoreName(), score.getValue());
                serverScoreboard.removeScore(score.getItemName());
                serverScoreboard.addScore(s);
                break;
            case 1:
                serverScoreboard.removeScore(score.getItemName());
                break;
            default:
                throw new IllegalArgumentException("Unknown scoreboard action: " + score.getAction());
        }
    }

    @Override
    public void handle(ScoreboardDisplay displayScoreboard) throws Exception {
        Scoreboard serverScoreboard = this.proxyClient.getScoreboard();
        serverScoreboard.setName(displayScoreboard.getName());
        serverScoreboard.setPosition(Position.values()[displayScoreboard.getPosition()]);
    }

    @Override
    public void handle(net.md_5.bungee.protocol.packet.Team team) throws Exception {
        Scoreboard serverScoreboard = this.proxyClient.getScoreboard();
        // Remove team and move on
        if (team.getMode() == 1) {
            serverScoreboard.removeTeam(team.getName());
            return;
        }

        // Create or get old team
        Team t;
        if (team.getMode() == 0) {
            t = new Team(team.getName());
            serverScoreboard.addTeam(t);
        } else {
            t = serverScoreboard.getTeam(team.getName());
        }

        if (t != null) {
            if (team.getMode() == 0 || team.getMode() == 2) {
                t.setDisplayName(team.getDisplayName());
                t.setPrefix(team.getPrefix());
                t.setSuffix(team.getSuffix());
                t.setFriendlyFire(team.getFriendlyFire());
                t.setNameTagVisibility(team.getNameTagVisibility());
                t.setCollisionRule(team.getCollisionRule());
                t.setColor(team.getColor());
            }
            if (team.getPlayers() != null) {
                for (String s : team.getPlayers()) {
                    if (team.getMode() == 0 || team.getMode() == 3) {
                        t.addPlayer(s);
                    } else if (team.getMode() == 4) {
                        t.removePlayer(s);
                    }
                }
            }
        }
    }

    @Override
    public void handle(Login login) throws Exception {
        this.proxyClient.setEntityId(login.getEntityId());
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (pluginMessage.getTag().equals( /*con.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_13 ? "minecraft:brand" : */"MC|Brand")) {
            ByteBuf brand = Unpooled.wrappedBuffer(pluginMessage.getData());
            String serverBrand = DefinedPacket.readString(brand);
            //brand.release();

            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString("RopProxy <- " + serverBrand, brand);
            pluginMessage.setData(DefinedPacket.toArray(brand));
            //brand.release();
            // changes in the packet are ignored so we need to send it manually
            if (this.con() != null) {
                this.con().unsafe().sendPacket(pluginMessage);
            }
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(Kick kick) throws Exception {
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
        ConnectedProxyClient proxyClient = MCProxy.getInstance().findBestProxyClient();
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
