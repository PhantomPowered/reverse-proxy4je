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
package com.github.derrop.proxy.plugins.gomme.secret;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.service.playerinfo.PlayerInfoRemoveEvent;
import com.github.derrop.proxy.api.events.connection.service.playerinfo.PlayerInfoUpdateEvent;
import com.github.derrop.proxy.api.events.connection.service.scoreboard.ScoreboardTeamUpdateEvent;
import com.github.derrop.proxy.api.scoreboard.Team;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.plugins.gomme.GommeStatsCore;
import com.github.derrop.proxy.plugins.gomme.parse.MatchParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GommeNickDetector extends MatchParser {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private long lastNickedJoin;
    private GommeNickProfile lastProfile;
    private Map<String, GommeNickProfile> nickProfiles = new ConcurrentHashMap<>(); // TODO should be ServiceConnection specific

    public GommeNickDetector(GommeStatsCore core) {
        super(core);
    }

    public Map<String, GommeNickProfile> getNickProfiles() {
        for (Map.Entry<String, GommeNickProfile> entry : this.nickProfiles.entrySet()) {
            if (entry.getValue().getNickName() == null) {
                this.nickProfiles.remove(entry.getKey());
            }
        }
        return this.nickProfiles; // TODO display on website, LabyMod, ...?
    }

    @Listener
    public void handleScoreboardTeamUpdate(ScoreboardTeamUpdateEvent event) {
        Team team = event.getTeam();

        if (team.getName().startsWith("ZZZ")) { // spectator teams start with ZZZ
            return;
        }
        if (team.getName().startsWith("npc")) { // NPCs
            return;
        }

        Set<String> entries = event.getTeam().getEntries();
        if (entries.size() != 1) {
            return;
        }

        String name = entries.iterator().next();

        if (name.trim().isEmpty() || name.indexOf((char) 167) != -1) {
            return;
        }

        Optional<GommeNickProfile> optionalProfile = this.nickProfiles.values().stream()
                .filter(profile -> profile.getRealTeam().getName().equals(team.getName()))
                .findFirst();
        if (optionalProfile.isPresent()) {
            optionalProfile.get().setRealTeam(team);
            return;
        }

        if (this.nickProfiles.containsKey(name) && this.nickProfiles.get(name).getNickName() != null) {
            return;
        }

        if (this.lastNickedJoin >= System.currentTimeMillis() - 3 && this.lastProfile != null) {
            GommeNickProfile profile = this.lastProfile;

            if (profile.getRealName().equals(name)) {
                return;
            }

            if (!team.getPrefix().contains("§6")) { // nicks are always premium members
                return;
            }

            this.lastProfile = null;

            System.out.println("Nick detected: " + ChatColor.stripColor(profile.getFullRealName()) + "; nicked as: " + name);

            if (event.getConnection().getPlayer() != null) {
                event.getConnection().getPlayer().sendMessage("§eGomme-Nick §8| §cNick §7detected: §e" + profile.getFullRealName() + " §7(probably the nick name is §e" + name + "§7)");
            }

            profile.setNickName(name);

            PlayerInfo playerInfo = event.getConnection().getWorldDataProvider().getOnlinePlayer(name);
            if (playerInfo != null) {
                this.sendTag(event.getConnection().getPlayer(), playerInfo.getUniqueId(), profile.getRealTeam(), profile.getRealName());
                profile.setNickInfo(playerInfo);
            }

            return;
        }

        if (event.getConnection().getWorldDataProvider().getOnlinePlayer(name) != null) {
            return;
        }

        if (team.getPrefix().contains("§a") || team.getPrefix().contains("§6")) { // players or premium members don't have nick permissions
            return;
        }

        this.lastNickedJoin = System.currentTimeMillis();
        GommeNickProfile profile = new GommeNickProfile(event.getConnection(), name, event.getTeam());
        this.lastProfile = profile;
        this.nickProfiles.put(profile.getRealName(), profile);
    }

    @Listener
    public void handlePlayerInfoRemove(PlayerInfoRemoveEvent event) {
        this.executorService.schedule(() -> {
            if (event.getConnection().getWorldDataProvider().getOnlinePlayer(event.getPlayerInfo().getUniqueId()) != null) { // maybe the server just went ingame? In BedWars players get randomly removed out of the player list and added again
                return;
            }
            for (GommeNickProfile profile : this.nickProfiles.values()) {
                if (profile.getNickName() != null && profile.getNickName().equals(event.getPlayerInfo().getUsername())) {
                    if (event.getConnection().getPlayer() != null) {
                        event.getConnection().getPlayer().sendMessage("§eGomme-Nick §8| §aUnNick/Disconnect §7detected: §e" + profile.getFullRealName() + " §7(probably the nick name is §e" + profile.getNickName() + "§7)");
                    }

                    System.out.println("UnNick/Disconnect detected: " + ChatColor.stripColor(profile.getFullRealName()) + " (Nick was: " + profile.getNickName() + ")");
                    if (event.getConnection().getPlayer() != null && profile.getNickInfo() != null) {
                        this.setSubtitle(event.getConnection().getPlayer(), profile.getNickInfo().getUniqueId(), null);
                    }
                    this.nickProfiles.values().remove(profile);
                }
            }
        }, 250, TimeUnit.MILLISECONDS);
    }

    private void sendTag(Player player, UUID uniqueId, Team realTeam, String realName) {
        if (player != null) {
            this.setSubtitle(player, uniqueId, realTeam.getPrefix() + realName + realTeam.getSuffix());
        }
    }

    private void setSubtitle(Player player, UUID uniqueId, String text) {
        JsonArray array = new JsonArray();

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uniqueId.toString());
        object.addProperty("size", 1.0D);
        if (text != null) {
            object.addProperty("value", text);
        }

        array.add(object);

        this.sendLMCMessage(player, "account_subtitle", array.toString());
    }

    private void sendLMCMessage(Player player, String key, String content) {
        ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeString(key, buf);
        ByteBufUtils.writeString(content, buf);

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        player.sendData("LMC", data);
    }

}
