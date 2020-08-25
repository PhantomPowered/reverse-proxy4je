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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[protoBuf.readVarInt()];
        this.items = new Item[protoBuf.readVarInt()];

        for (int i = 0; i < items.length; i++) {
            Item item = items[i] = new Item();
            item.setUniqueId(protoBuf.readUniqueId());

            switch (action) {
                case ADD_PLAYER:
                    item.username = protoBuf.readString();
                    item.properties = new String[protoBuf.readVarInt()][];

                    for (int j = 0; j < item.properties.length; j++) {
                        String name = protoBuf.readString();
                        String value = protoBuf.readString();

                        if (protoBuf.readBoolean()) {
                            item.properties[j] = new String[]{
                                    name, value, protoBuf.readString()
                            };
                        } else {
                            item.properties[j] = new String[]{
                                    name, value
                            };
                        }
                    }

                    item.gamemode = protoBuf.readVarInt();
                    item.ping = protoBuf.readVarInt();

                    if (protoBuf.readBoolean()) {
                        item.displayName = protoBuf.readString();
                    }

                    break;

                case UPDATE_GAMEMODE:
                    item.gamemode = protoBuf.readVarInt();
                    break;

                case UPDATE_LATENCY:
                    item.ping = protoBuf.readVarInt();
                    break;

                case UPDATE_DISPLAY_NAME:
                    if (protoBuf.readBoolean()) {
                        item.displayName = protoBuf.readString();
                    }

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.action.ordinal());
        protoBuf.writeVarInt(this.items.length);

        for (Item item : this.items) {
            protoBuf.writeUniqueId(item.uniqueId);

            switch (action) {
                case ADD_PLAYER:
                    protoBuf.writeString(item.username);
                    protoBuf.writeVarInt(item.properties.length);

                    for (String[] property : item.properties) {
                        protoBuf.writeString(property[0]);
                        protoBuf.writeString(property[1]);

                        if (property.length >= 3) {
                            protoBuf.writeBoolean(true);
                            protoBuf.writeString(property[2]);
                        } else {
                            protoBuf.writeBoolean(false);
                        }
                    }

                    protoBuf.writeVarInt(item.gamemode);
                    protoBuf.writeVarInt(item.ping);
                    protoBuf.writeBoolean(item.displayName != null);

                    if (item.displayName != null) {
                        protoBuf.writeString(item.displayName);
                    }
                    break;

                case UPDATE_GAMEMODE:
                    protoBuf.writeVarInt(item.gamemode);
                    break;

                case UPDATE_LATENCY:
                    protoBuf.writeVarInt(item.ping);
                    break;

                case UPDATE_DISPLAY_NAME:
                    protoBuf.writeBoolean(item.displayName != null);
                    if (item.displayName != null) {
                        protoBuf.writeString(item.displayName);
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

        private UUID uniqueId;
        private String username;
        private String[][] properties;
        private int gamemode;
        private int ping;
        private String displayName;

        public Item(UUID uniqueId, String username, String[][] properties, int gamemode, int ping, String displayName) {
            this.uniqueId = uniqueId;
            this.username = username;
            this.properties = properties;
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
            return this.uniqueId;
        }

        public String getUsername() {
            return this.username;
        }

        public String[][] getProperties() {
            return this.properties;
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

        public void setUniqueId(UUID uniqueId) {
            this.uniqueId = uniqueId;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setProperties(String[][] properties) {
            this.properties = properties;
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
                    + ", properties=" + Arrays.deepToString(this.getProperties())
                    + ", gamemode=" + this.getGamemode()
                    + ", ping=" + this.getPing()
                    + ", displayName=" + this.getDisplayName()
                    + ")";
        }
    }
}
