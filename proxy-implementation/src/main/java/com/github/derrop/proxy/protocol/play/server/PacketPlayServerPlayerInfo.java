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
package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketPlayServerPlayerInfo implements Packet {

    private Action action;
    private Item[] items;

    public PacketPlayServerPlayerInfo(Action action, Item[] items) {
        this.action = action;
        this.items = items;
    }

    public PacketPlayServerPlayerInfo() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.PLAYER_INFO;
    }

    public Action getAction() {
        return this.action;
    }

    public Item[] getItems() {
        return this.items;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[buf.readVarInt()];
        this.items = new Item[buf.readVarInt()];

        for (int i = 0; i < items.length; i++) {
            Item item = items[i] = new Item();
            UUID uniqueId = buf.readUniqueId();

            if (action != Action.ADD_PLAYER) {
                item.profile = new GameProfile(uniqueId, "null");
            }

            switch (action) {
                case ADD_PLAYER:
                    item.profile = new GameProfile(uniqueId, buf.readString());

                    PropertyMap properties = item.profile.getProperties();
                    int size = buf.readVarInt();

                    for (int j = 0; j < size; j++) {
                        String name = buf.readString();
                        String value = buf.readString();
                        String signature = buf.readBoolean() ? buf.readString() : null;

                        properties.put(name, signature != null ? new Property(name, value, signature) : new Property(name, value));
                    }

                    item.gamemode = buf.readVarInt();
                    item.ping = buf.readVarInt();

                    if (buf.readBoolean()) {
                        item.displayName = buf.readString();
                    }

                    break;

                case UPDATE_GAMEMODE:
                    item.gamemode = buf.readVarInt();
                    break;

                case UPDATE_LATENCY:
                    item.ping = buf.readVarInt();
                    break;

                case UPDATE_DISPLAY_NAME:
                    if (buf.readBoolean()) {
                        item.displayName = buf.readString();
                    }

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeVarInt(this.action.ordinal());
        buf.writeVarInt(this.items.length);

        for (Item item : this.items) {
            buf.writeUniqueId(item.getUniqueId());

            switch (action) {
                case ADD_PLAYER:
                    buf.writeString(item.getUsername());
                    PropertyMap properties = item.getProfile().getProperties();

                    buf.writeVarInt(properties.size());

                    for (Property property : properties.values()) {
                        buf.writeString(property.getName());
                        buf.writeString(property.getValue());

                        buf.writeBoolean(property.hasSignature());
                        if (property.hasSignature()) {
                            buf.writeString(property.getSignature());
                        }
                    }

                    buf.writeVarInt(item.gamemode);
                    buf.writeVarInt(item.ping);
                    buf.writeBoolean(item.displayName != null);

                    if (item.displayName != null) {
                        buf.writeString(item.displayName);
                    }
                    break;

                case UPDATE_GAMEMODE:
                    buf.writeVarInt(item.gamemode);
                    break;

                case UPDATE_LATENCY:
                    buf.writeVarInt(item.ping);
                    break;

                case UPDATE_DISPLAY_NAME:
                    buf.writeBoolean(item.displayName != null);
                    if (item.displayName != null) {
                        buf.writeString(item.displayName);
                    }

                    break;

                default:
                    break;
            }
        }
    }

    public String toString() {
        return "PacketPlayServerPlayerInfo(action=" + this.getAction() + ", items=" + java.util.Arrays.deepToString(this.getItems()) + ")";
    }

    public enum Action {

        ADD_PLAYER,
        UPDATE_GAMEMODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER
    }

    public static class Item implements Cloneable {

        private GameProfile profile;
        private int gamemode;
        private int ping;
        private String displayName;

        public Item(GameProfile profile, int gamemode, int ping, String displayName) {
            this.profile = profile;
            this.gamemode = gamemode;
            this.ping = ping;
            this.displayName = displayName;
        }

        public Item() {
        }

        @Override
        public Item clone() {
            try {
                return (Item) super.clone();
            } catch (CloneNotSupportedException exception) {
                exception.printStackTrace();
            }
            return null;
        }

        public UUID getUniqueId() {
            return this.profile.getId();
        }

        public void setUniqueId(UUID uniqueId) {
            PropertyMap map = this.profile.getProperties();
            this.profile = new GameProfile(uniqueId, this.profile.getName());
            this.profile.getProperties().putAll(map);
        }

        public String getUsername() {
            return this.profile.getName();
        }

        public int getGamemode() {
            return this.gamemode;
        }

        public int getPing() {
            return this.ping;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public void setGamemode(int gamemode) {
            this.gamemode = gamemode;
        }

        public void setPing(int ping) {
            this.ping = ping;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String toString() {
            return "PacketPlayServerPlayerInfo.Item(uuid=" + this.getUniqueId()
                    + ", username=" + this.getUsername()
                    + ", properties=" + this.getProfile().getProperties()
                    + ", gamemode=" + this.getGamemode()
                    + ", ping=" + this.getPing()
                    + ", displayName=" + this.getDisplayName()
                    + ")";
        }
    }
}
