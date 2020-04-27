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

import com.github.derrop.proxy.api.entity.player.Player;
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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GommeNickDetector extends MatchParser {

    private long lastNickedJoin;
    private GommeNickProfile lastProfile;
    private Map<String, GommeNickProfile> nickProfiles = new ConcurrentHashMap<>();

    public GommeNickDetector(GommeStatsCore core) {
        super(core);
    }

    public Map<String, GommeNickProfile> getNickProfiles() {
        return this.nickProfiles; // TODO display on website, LabyMod, ...?
    }

    @Listener
    public void handleScoreboardTeamUpdate(ScoreboardTeamUpdateEvent event) {
        if (event.getTeam().getProperties().containsKey("GommeNicked")) {
            this.nickProfiles.values().stream()
                    .filter(profile -> profile.getRealTeam() != null && profile.getRealTeam().getName().equals(event.getTeam().getName()))
                    .forEach(profile -> profile.setRealTeam(event.getTeam()));
            return;
        }

        Set<String> entries = event.getTeam().getEntries();
        if (entries.size() != 1) {
            return;
        }

        String name = entries.iterator().next();

        if (name.indexOf((char) 167) != -1) {
            return;
        }

        if (this.lastNickedJoin >= System.currentTimeMillis() - 3 && this.lastProfile != null) {
            GommeNickProfile profile = this.lastProfile;
            this.lastProfile = null;

            profile.setNickName(name);

            Arrays.stream(event.getConnection().getWorldDataProvider().getOnlinePlayers())
                    .filter(playerInfo -> playerInfo.getUsername().equals(name))
                    .findFirst()
                    .ifPresent(playerInfo -> {
                        this.sendTag(event.getConnection().getPlayer(), playerInfo.getUniqueId(), profile.getRealTeam(), profile.getRealName());
                        profile.setNickInfo(playerInfo);
                    });

            return;
        }

        if (event.getTeam().getName().startsWith("ZZZ")) { // spectator teams start with ZZZ
            return;
        }
        if (event.getTeam().getName().startsWith("npc")) { // NPCs
            return;
        }

        boolean present = Arrays.stream(event.getConnection().getWorldDataProvider().getOnlinePlayers())
                .anyMatch(playerInfo -> playerInfo.getUsername().equals(name));
        if (present) {
            return;
        }

        System.out.println("Nick detected: " + name);
        event.getTeam().getProperties().put("GommeNicked", true);

        this.lastNickedJoin = System.currentTimeMillis();
        GommeNickProfile profile = new GommeNickProfile(name, event.getTeam());
        this.lastProfile = profile;
        this.nickProfiles.put(profile.getRealName(), profile);
    }

    @Listener
    public void handlePlayerInfoRemove(PlayerInfoRemoveEvent event) {
        for (GommeNickProfile profile : this.nickProfiles.values()) {
            if (profile.getNickName() != null && profile.getNickName().equals(event.getPlayerInfo().getUsername())) {
                System.out.println("UnNick/Disconnect detected: " + profile.getRealName() + " (Nick was: " + profile.getNickName() + ")");
                if (event.getConnection().getPlayer() != null && profile.getNickInfo() != null) {
                    this.setSubtitle(event.getConnection().getPlayer(), profile.getNickInfo().getUniqueId(), null);
                }
                this.nickProfiles.values().remove(profile);
            }
        }
    }

    @Listener
    public void handlePlayerInfoUpdate(PlayerInfoUpdateEvent event) {
        this.nickProfiles.values().stream()
                .filter(profile -> profile.getNickName() != null && profile.getNickName().equals(event.getPlayerInfo().getUsername()))
                .forEach(profile -> {
                    if (profile.getNickInfo() == null) {
                        this.sendTag(event.getConnection().getPlayer(), event.getPlayerInfo().getUniqueId(), profile.getRealTeam(), profile.getRealName());
                    }
                    profile.setNickInfo(event.getPlayerInfo());
                });
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
