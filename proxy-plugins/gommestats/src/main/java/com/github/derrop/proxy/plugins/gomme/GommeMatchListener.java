/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.EntityPlayer;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerMoveEvent;
import com.github.derrop.proxy.api.events.connection.service.entity.EntityMoveEvent;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.plugins.gomme.match.MatchInfo;
import com.github.derrop.proxy.plugins.gomme.match.MatchManager;
import com.github.derrop.proxy.plugins.gomme.match.event.cores.CoreJoinEvent;
import com.github.derrop.proxy.plugins.gomme.match.event.cores.CoreLeaveEvent;
import com.github.derrop.proxy.plugins.gomme.match.messages.Language;
import com.github.derrop.proxy.plugins.gomme.match.messages.MessageRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GommeMatchListener {

    private final MatchManager matchManager;

    public GommeMatchListener(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Listener
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || !event.getTag().equals("GoMod")) {
            return;
        }

        ByteBuf buf = Unpooled.wrappedBuffer(event.getData());

        JsonObject jsonObject = JsonParser.parseString(ByteBufUtils.readString(buf)).getAsJsonObject();

        String action = jsonObject.get("action").getAsString().toUpperCase();
        JsonObject data = jsonObject.has("data") ? jsonObject.get("data").getAsJsonObject() : null;

        if (data != null && action.equals("JOIN_SERVER")) {
            String serverType = data.get("cloud_type").getAsString().toUpperCase();
            String matchId = data.get("id").getAsString();

            this.matchManager.deleteMatch((ServiceConnection) event.getConnection());

            if (this.matchManager.getMatch(matchId) != null) {
                return;
            }

            GommeGameMode gameMode = GommeGameMode.getByGommeName(serverType);
            if (gameMode == null) {
                return;
            }

            this.matchManager.createMatch(new MatchInfo(
                    (ServiceConnection) event.getConnection(),
                    gameMode,
                    matchId
            ));
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                System.out.println("MatchBegin on " + gameMode + ": " + Arrays.stream(((ServiceConnection) event.getConnection()).getWorldDataProvider().getOnlinePlayers()).map(playerInfo -> playerInfo.getUniqueId() + "#" + playerInfo.getUsername()).collect(Collectors.joining(", ")));
            });
        }
    }

    @Listener
    public void handleMove(EntityMoveEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();

        PlayerInfo playerInfo = player.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }

        this.handleMove(event.getConnection(), playerInfo.getUsername(), event.getFrom(), event.getTo());
    }

    @Listener
    public void handleMove(PlayerMoveEvent event) {
        if (event.getConnection() instanceof Player) {
            this.handleMove(((Player) event.getConnection()).getConnectedClient(), event.getPlayer().getName(), event.getFrom(), event.getTo());
        }
    }

    private void handleMove(ServiceConnection connection, String playerName, Location from, Location to) {
        MatchInfo match = this.matchManager.getMatch(connection);
        if (match == null || match.getGameMode() != GommeGameMode.CORES) {
            return;
        }

        Collection<BlockPos> cores = connection.getBlockAccess().getPositions(Material.BEACON);

        if (cores.isEmpty()) {
            return;
        }

        for (BlockPos core : cores) {
            boolean toNearby = core.distanceSq(to.toBlockPos()) < GommeConstants.CORE_NEARBY_DISTANCE_SQ;
            boolean fromNearby = core.distanceSq(from.toBlockPos()) < GommeConstants.CORE_NEARBY_DISTANCE_SQ;

            if (fromNearby && !toNearby) {
                match.callEvent(new CoreLeaveEvent(playerName, core));
            } else if (toNearby && !fromNearby) {
                match.callEvent(new CoreJoinEvent(playerName, core));
            }
        }
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || event.getType() != ChatMessageType.CHAT) {
            return;
        }

        MatchInfo match = this.matchManager.getMatch((ServiceConnection) event.getConnection());
        if (match == null) {
            return;
        }

        String message = ChatColor.stripColor(LegacyComponentSerializer.legacy().serialize(event.getMessage()));

        // TODO Language should be dynamic
        MessageRegistry.createMatchEvent(Language.GERMAN, match.getGameMode(), message).ifPresent(match::callEvent);

    }

}
