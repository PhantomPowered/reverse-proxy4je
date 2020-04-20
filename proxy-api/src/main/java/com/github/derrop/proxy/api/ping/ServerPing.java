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
package com.github.derrop.proxy.api.ping;

import net.kyori.text.Component;

import java.util.UUID;

public class ServerPing implements Cloneable {

    private Protocol version;
    private Players players;
    private Component description;
    private Favicon favicon;

    public ServerPing(Protocol version, Players players, Component description, Favicon favicon) {
        this.version = version;
        this.players = players;
        this.description = description;
        this.favicon = favicon;
    }

    public Protocol getVersion() {
        return version;
    }

    public void setVersion(Protocol version) {
        this.version = version;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public Component getDescription() {
        return description;
    }

    public void setDescription(Component description) {
        this.description = description;
    }

    public Favicon getFavicon() {
        return favicon;
    }

    public void setFavicon(Favicon favicon) {
        this.favicon = favicon;
    }

    @Override
    public ServerPing clone() {
        try {
            return (ServerPing) super.clone();
        } catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static class Protocol {

        private String name;
        private int protocol;

        public Protocol(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getProtocol() {
            return protocol;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }
    }

    public static class Players {

        private int online;
        private int max;
        private PlayerInfo[] sample;

        public Players(int online, int max, PlayerInfo[] sample) {
            this.online = online;
            this.max = max;
            this.sample = sample;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public PlayerInfo[] getSample() {
            return sample;
        }

        public void setSample(PlayerInfo[] sample) {
            this.sample = sample;
        }

        public static class PlayerInfo {

            private String name;
            private UUID id;

            public PlayerInfo(String name, UUID id) {
                this.name = name;
                this.id = id;
            }

            public PlayerInfo(String name) {
                this(name, UUID.randomUUID());
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public UUID getId() {
                return id;
            }

            public void setId(UUID id) {
                this.id = id;
            }
        }

    }

}
