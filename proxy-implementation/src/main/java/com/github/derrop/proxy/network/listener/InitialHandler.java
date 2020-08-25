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
package com.github.derrop.proxy.network.listener;

import com.github.derrop.proxy.ImplementationUtil;
import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.concurrent.Callback;
import com.github.derrop.proxy.api.configuration.Configuration;
import com.github.derrop.proxy.api.connection.*;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.PingEvent;
import com.github.derrop.proxy.api.events.connection.ServiceConnectorChooseClientEvent;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.player.OfflinePlayer;
import com.github.derrop.proxy.api.player.PlayerRepository;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.connection.LoginResult;
import com.github.derrop.proxy.connection.handler.ClientChannelListener;
import com.github.derrop.proxy.connection.player.DefaultOfflinePlayer;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.http.HttpUtil;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.pipeline.cipher.PacketCipherDecoder;
import com.github.derrop.proxy.network.pipeline.cipher.PacketCipherEncoder;
import com.github.derrop.proxy.network.pipeline.encryption.ServerEncryptionUtils;
import com.github.derrop.proxy.network.pipeline.handler.HandlerEndpoint;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingClientSetProtocol;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInLoginRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutPong;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInPing;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInRequest;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;

public class InitialHandler {

    protected static final String INIT_STATE = "initialState";
    private final ServiceRegistry serviceRegistry;

    public InitialHandler(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Status.START, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.STATUS)
    public void handle(NetworkChannel channel, PacketStatusInRequest statusRequest) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.STATUS, "Not expecting STATUS");

        ServerPing response = this.serviceRegistry.getProviderUnchecked(Configuration.class).getMotd();
        ServiceConnector connector = this.serviceRegistry.getProviderUnchecked(ServiceConnector.class);

        response.setDescription(TextComponent.of(LegacyComponentSerializer.legacySection().serialize(response.getDescription())
                .replace("$free", String.valueOf(connector.getFreeClients().size()))
                .replace("$online", String.valueOf(connector.getOnlineClients().size()))
        ));

        PingEvent event = this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new PingEvent(channel, response));
        if (event.getResponse() == null) {
            channel.close();
            return;
        }

        channel.write(new PacketStatusOutResponse(ImplementationUtil.GSON.toJson(event.getResponse())));

        channel.setProperty(INIT_STATE, State.PING);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Status.PING, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.STATUS)
    public void handle(NetworkChannel channel, PacketStatusInPing ping) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.PING, "Not expecting PING");
        channel.write(new PacketStatusOutPong(ping.getTime()));
        disconnect(channel, "");
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Handshaking.SET_PROTOCOL, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.HANDSHAKING)
    public void handle(NetworkChannel channel, PacketHandshakingClientSetProtocol packetHandshakingClientSetProtocol) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.HANDSHAKE, "Not expecting HANDSHAKE");
        channel.setProperty("sentProtocol", packetHandshakingClientSetProtocol.getProtocolVersion());

        // Starting with FML 1.8, a "\0FML\0" token is appended to the handshake. This interferes
        // with Bungee's IP forwarding, so we detect it, and remove it from the host string, for now.
        // We know FML appends \00FML\00. However, we need to also consider that other systems might
        // add their own data to the end of the string. So, we just take everything from the \0 character
        // and save it for later.
        if (packetHandshakingClientSetProtocol.getHost().contains("\0")) {
            String[] split = packetHandshakingClientSetProtocol.getHost().split("\0", 2);
            packetHandshakingClientSetProtocol.setHost(split[0]);
            channel.setProperty("extraDataInHandshake", "\0" + split[1]);
        }

        // SRV records can end with a . depending on DNS / client.
        if (packetHandshakingClientSetProtocol.getHost().endsWith(".")) {
            packetHandshakingClientSetProtocol.setHost(packetHandshakingClientSetProtocol.getHost().substring(0, packetHandshakingClientSetProtocol.getHost().length() - 1));
        }

        channel.setProperty("virtualHost", InetSocketAddress.createUnresolved(packetHandshakingClientSetProtocol.getHost(), packetHandshakingClientSetProtocol.getPort()));

        switch (packetHandshakingClientSetProtocol.getRequestedProtocol()) {
            case 1:
                channel.setProperty(INIT_STATE, State.STATUS);
                channel.setProtocolState(ProtocolState.STATUS);
                break;

            case 2:
                channel.setProperty(INIT_STATE, State.USERNAME);
                channel.setProtocolState(ProtocolState.LOGIN);
                if (packetHandshakingClientSetProtocol.getProtocolVersion() != ProtocolIds.Versions.MINECRAFT_1_8) {
                    disconnect(channel, "We only support 1.8");
                    return;
                }
                break;

            default:
                disconnect(channel, "Cannot request protocol " + packetHandshakingClientSetProtocol.getRequestedProtocol());
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Login.START, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.LOGIN)
    public void handle(NetworkChannel channel, PacketLoginInLoginRequest loginRequest) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.USERNAME, "Not expecting USERNAME");
        channel.setProperty("requestedName", loginRequest.getData());

        if (loginRequest.getData().contains(".")) {
            disconnect(channel, "invalid name");
            return;
        }

        if (loginRequest.getData().length() > 16) {
            disconnect(channel, "name too long");
            return;
        }

        PacketLoginInEncryptionRequest request = ServerEncryptionUtils.getLoginEncryptionRequestPacket();
        channel.setProperty("encryptionRequest", request);
        channel.write(request);
        channel.setProperty(INIT_STATE, State.ENCRYPT);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Login.ENCRYPTION_RESPONSE, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.LOGIN)
    public void handle(NetworkChannel channel, final PacketLoginOutEncryptionResponse encryptResponse) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.ENCRYPT, "Not expecting ENCRYPT");

        PacketLoginInEncryptionRequest encryptionRequest = channel.getProperty("encryptionRequest");
        SecretKey sharedKey = ServerEncryptionUtils.getSecretKey(encryptResponse, encryptionRequest);
        if (sharedKey == null) {
            channel.close();
            return;
        }

        channel.getWrappedChannel().pipeline().addBefore(NetworkUtils.LENGTH_DECODER, NetworkUtils.DECRYPT, new PacketCipherDecoder(sharedKey));
        channel.getWrappedChannel().pipeline().addBefore(NetworkUtils.LENGTH_ENCODER, NetworkUtils.ENCRYPT, new PacketCipherEncoder(sharedKey));

        String encName = URLEncoder.encode(channel.getProperty("requestedName"), "UTF-8");

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        for (byte[] bit : new byte[][]{
                encryptionRequest.getServerId().getBytes("ISO_8859_1"),
                sharedKey.getEncoded(),
                ServerEncryptionUtils.KEY_PAIR.getPublic().getEncoded()
        }) {
            sha.update(bit);
        }
        String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");

        //String preventProxy = (getSocketAddress() instanceof InetSocketAddress) ? "&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8") : "";
        String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash;// + preventProxy;

        Callback<String> handler = (result, error) -> {
            if (error == null) {
                LoginResult obj = ImplementationUtil.GSON.fromJson(result, LoginResult.class);
                if (obj != null && obj.getId() != null) {
                    UUID uniqueId = ImplementationUtil.parseUUID(obj.getId());
                    /*if (uniqueId == null) {
                        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
                    }*/
                    finish(channel, uniqueId, obj);
                    return;
                }
                disconnect(channel, "offline mode not supported");
            } else {
                disconnect(channel, "failed to authenticate with mojang");
                error.printStackTrace();
            }
        };

        HttpUtil.get(authURL, handler);
    }

    private void finish(NetworkChannel channel, UUID uniqueId, LoginResult result) {
        if (this.serviceRegistry.getProviderUnchecked(PlayerRepository.class).getOnlinePlayer(uniqueId) != null) {
            disconnect(channel, "Already connected");
            return;
        }

        Optional<Whitelist> whitelist = this.serviceRegistry.getProvider(Whitelist.class);
        if (whitelist.isPresent() && whitelist.get().isEnabled() && !whitelist.get().isWhitelisted(uniqueId)) {
            disconnect(channel, "The whitelist is enabled and you're not whitelisted");
            return;
        }

        channel.getWrappedChannel().eventLoop().execute(() -> {
            if (!channel.isClosing()) {
                PlayerRepository repository = this.serviceRegistry.getProviderUnchecked(PlayerRepository.class);
                OfflinePlayer offlinePlayer = repository.getOfflinePlayer(uniqueId);
                if (offlinePlayer == null) {
                    offlinePlayer = new DefaultOfflinePlayer(uniqueId, result.getName(), -1, -1);
                    repository.insertOfflinePlayer(offlinePlayer);
                }

                ServiceConnection client = this.serviceRegistry.getProviderUnchecked(ServiceConnector.class).findBestConnection(offlinePlayer.getUniqueId());
                ServiceConnectorChooseClientEvent clientEvent = this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new ServiceConnectorChooseClientEvent(uniqueId, client));
                client = clientEvent.getConnection();
                if (client == null || clientEvent.isCancelled()) {
                    disconnect(channel, TextComponent.of("§7No client found"));
                    return;
                }

                DefaultPlayer player = new DefaultPlayer(this.serviceRegistry, ((BasicServiceConnection) client).getClient(), offlinePlayer, result, channel, channel.getProperty("sentProtocol"), 256);
                repository.updateOfflinePlayer(player);

                PlayerLoginEvent event = this.serviceRegistry.getProviderUnchecked(EventManager.class).callEvent(new PlayerLoginEvent(player));
                if (!channel.isConnected()) {
                    return;
                }

                if (event.isCancelled()) {
                    disconnect(channel, event.getCancelReason() == null ? TextComponent.of("§cNo reason given") : event.getCancelReason());
                    return;
                }

                channel.write(new PacketLoginOutLoginSuccess(uniqueId.toString(), result.getName())); // With dashes in between
                channel.setProtocolState(ProtocolState.PLAY);
                channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setNetworkChannel(player);
                channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setChannelListener(new ClientChannelListener(player));

                player.useClient(client);
                channel.setProperty(INIT_STATE, State.FINISHED);
            }
        });
    }

    static boolean canSendKickMessage(NetworkChannel channel) {
        State state = channel.getProperty(INIT_STATE);
        return state == State.USERNAME || state == State.ENCRYPT || state == State.FINISHED;
    }

    static void disconnect(NetworkChannel channel, @NotNull String reason) {
        if (canSendKickMessage(channel)) {
            disconnect(channel, TextComponent.of(APIUtil.MESSAGE_PREFIX + reason));
        } else {
            channel.close();
        }
    }

    static void disconnect(NetworkChannel channel, final Component reason) {
        if (canSendKickMessage(channel)) {
            if (channel.getProtocolState() == ProtocolState.PLAY) {
                channel.delayedClose(new PacketPlayServerKickPlayer(GsonComponentSerializer.gson().serialize(reason)));
            } else {
                channel.delayedClose(new PacketLoginOutServerKickPlayer(GsonComponentSerializer.gson().serialize(reason)));
            }
        } else {
            channel.close();
        }
    }

    enum State {

        HANDSHAKE,
        STATUS,
        PING,
        USERNAME,
        ENCRYPT,
        FINISHED
    }
}
