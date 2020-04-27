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
package com.github.derrop.proxy.plugins.replay;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.network.wrapper.DefaultProtoBuf;
import com.github.derrop.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.derrop.proxy.protocol.play.server.entity.spawn.PacketPlayServerNamedEntitySpawn;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.derrop.proxy.util.serialize.SerializableObject;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ReplaySystem { // TODO

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private Path replayDirectory;

    private Map<ReplayInfo, ReplayOutputStream> runningReplays = new ConcurrentHashMap<>();

    public ReplaySystem() throws IOException {
        this(Paths.get("replays"));
    }

    public ReplaySystem(Path path) throws IOException {
        Files.createDirectories(this.replayDirectory = path);
    }

    private Path getPath(UUID replayId) throws ReplayNotFoundException {
        Path path = this.replayDirectory.resolve(replayId + ".replay");
        if (!Files.exists(path)) {
            throw new ReplayNotFoundException(replayId);
        }
        return path;
    }

    public Map<ReplayInfo, ReplayOutputStream> getRunningReplays() {
        return runningReplays;
    }

    public ReplayInfo readReplayInfo(UUID replayId) {
        try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(this.getPath(replayId)))) {
            ReplayInfo replayInfo = new ReplayInfo();
            replayInfo.read(inputStream);
            return replayInfo;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public UUID[] listReplays() {
        try {
            return Files.list(this.replayDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(s -> s.replace(".replay", ""))
                    .map(UUID::fromString)
                    .toArray(UUID[]::new);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return new UUID[0];
    }

    public UUID recordReplay(BasicServiceConnection connection, UUID creatorId, String creatorName) throws IOException, ReplayNotFoundException {
        UUID replayId = UUID.randomUUID();

        OutputStream outputStream = Files.newOutputStream(this.replayDirectory.resolve(replayId + ".replay"));

        this.recordReplay(connection, creatorId, creatorName, outputStream);

        return replayId;
    }

    public CompletableFuture<Boolean> playReplayAsync(Player player, UUID replayId, Consumer<ReplayInfo> infoConsumer) throws ReplayNotFoundException {
        this.getPath(replayId);

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        this.executorService.execute(() -> {
            try {
                this.playReplay(player, replayId, infoConsumer);

                future.complete(true);
            } catch (IOException exception) {
                exception.printStackTrace();

                future.complete(false);
            }

        });

        return future;
    }

    public void playReplay(Player player, UUID replayId, Consumer<ReplayInfo> infoConsumer) throws IOException, ReplayNotFoundException {
        InputStream inputStream = Files.newInputStream(this.getPath(replayId));

        this.playReplay(player, inputStream, infoConsumer);
    }

    public void playReplay(Player player, InputStream inputStream, Consumer<ReplayInfo> infoConsumer) throws IOException {
        ReplayInputStream replayInputStream = new ReplayInputStream(inputStream);

        infoConsumer.accept(replayInputStream.getReplayInfo());

        this.executorService.execute(() -> {
            while (!replayInputStream.isClosed()) {
                player.sendPacket(new PacketPlayKeepAlive(System.nanoTime()));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });

        long recBegin = replayInputStream.getReplayInfo().getTimestamp();
        long playBegin = System.currentTimeMillis();
        long diff = playBegin - recBegin;
        if (diff < 0) {
            throw new IllegalArgumentException("Cannot play replays out of the future");
        }

        ReplayPacket packet;
        while ((packet = replayInputStream.readPacket()) != null) {
            long targetTime = packet.getTimestamp() + diff;
            long current = System.currentTimeMillis();

            if (targetTime > current) {
                try {
                    Thread.sleep(targetTime - current);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }

            player.networkUnsafe().sendPacket(Unpooled.wrappedBuffer(packet.getData()));
            /*ByteBuf buf = Unpooled.wrappedBuffer(packet.getData());
            buf.markReaderIndex();

            int id = DefinedPacket.readVarInt(buf);

            if (id == PacketConstants.SPAWN_PLAYER) {
                SpawnPlayer spawnPlayer = new SpawnPlayer();
                spawnPlayer.read(buf, ProtocolConstants.Direction.TO_CLIENT, 47);

                if (spawnPlayer.getEntityId() == replayInputStream.getReplayInfo().getOwnEntityId()) {
                    player.sendPacket(new EntityTeleport(
                            player.getClientEntityId(),
                            spawnPlayer.getX(), spawnPlayer.getY(), spawnPlayer.getZ(), spawnPlayer.getYaw(), spawnPlayer.getPitch(), false
                    ));
                }
            }

            buf.resetReaderIndex();

            player.sendPacket(buf);*/
        }
    }

    public ReplayOutputStream recordReplay(BasicServiceConnection connection, UUID creatorId, String creatorName, OutputStream outputStream) throws IOException {
        ConnectedProxyClient proxyClient = connection.getClient();
        Preconditions.checkArgument(proxyClient.getPacketCache().getPacketHandler() == null, "already recording a replay for that client");

        ReplayInfo replayInfo = new ReplayInfo(proxyClient.getServerAddress(), creatorId, creatorName, proxyClient.getCredentials(), System.currentTimeMillis(), proxyClient.getEntityId());

        ReplayOutputStream replayOutputStream = new ReplayOutputStream(replayInfo, this.executorService, outputStream);

        this.runningReplays.put(replayInfo, replayOutputStream);

        replayOutputStream.setCloseHandler(() -> {
            this.runningReplays.remove(replayInfo);
            proxyClient.getPacketCache().setPacketHandler(null);
            proxyClient.setDisconnectionHandler(null);
        });

        PacketSender receiver = new PacketSender() {
            @Override
            public void sendPacket(@NotNull Packet packet) {
                this.networkUnsafe().sendPacket(packet);
            }

            @Override
            public void sendPacket(@NotNull ByteBuf byteBuf) {
                this.networkUnsafe().sendPacket(byteBuf);
            }

            @Override
            public @NotNull NetworkUnsafe networkUnsafe() {
                return packet -> {
                    if (replayOutputStream.isClosed()) {
                        return;
                    }

                    ByteBuf buf;

                    if (packet instanceof Packet) {
                        buf = Unpooled.buffer();

                        Packet definedPacket = (Packet) packet;

                        ByteBufUtils.writeVarInt(definedPacket.getId(), buf);

                        definedPacket.write(new DefaultProtoBuf(47, buf), ProtocolDirection.TO_CLIENT, 47);
                    } else if (packet instanceof ByteBuf) {
                        buf = ((ByteBuf) packet).copy();
                    } else {
                        throw new IllegalArgumentException("Invalid packet received");
                    }

                    buf.markReaderIndex();

                    int packetId = ByteBufUtils.readVarInt(buf);

                    buf.resetReaderIndex();

                    if (!ReplayIDs.isReplayPacket(packetId)) {
                        return;
                    }

                    byte[] data = new byte[buf.readableBytes()];
                    buf.readBytes(data);

                    replayOutputStream.write(new ReplayPacket(data, System.currentTimeMillis()));
                };
            }
        };
        for (PacketCacheHandler handler : proxyClient.getPacketCache().getHandlers()) {
            handler.sendCached(receiver);
        }
        receiver.sendPacket(new PacketPlayServerNamedEntitySpawn(proxyClient.getEntityId(), UUID.fromString("fdef0011-1c58-40c8-bfef-0bdcb1495938"),
                connection.getLocation(),
                (short) 0,
                Arrays.asList(
                        new SerializableObject(2, 18, 0),
                        new SerializableObject(3, 17, 0F),
                        new SerializableObject(0, 16, (byte) 0),
                        new SerializableObject(0, 10, (byte) 127),
                        new SerializableObject(0, 9, (byte) 0),
                        new SerializableObject(0, 8, (byte) 0),
                        new SerializableObject(2, 7, 0),
                        new SerializableObject(3, 6, 20F),
                        new SerializableObject(0, 4, (byte) 0),
                        new SerializableObject(0, 3, (byte) 0),
                        new SerializableObject(4, 2, ""),
                        new SerializableObject(1, 1, (short) 300),
                        new SerializableObject(0, 0, (byte) 0)
                )
        ));
        receiver.sendPacket(proxyClient.getEntityMetadata());

        proxyClient.setClientPacketHandler(packet -> {
            if (packet instanceof PositionedPacket) {
                PositionedPacket positionedPacket = (PositionedPacket) packet;
                receiver.sendPacket(new PacketPlayServerEntityTeleport(proxyClient.getEntityId(),
                        positionedPacket.getX(), positionedPacket.getY(), positionedPacket.getZ(),
                        positionedPacket.getYaw(), positionedPacket.getPitch(),
                        connection.isOnGround()
                ));
            }
        });
        proxyClient.getPacketCache().setPacketHandler((buf, packetId) -> receiver.sendPacket(buf));
        proxyClient.setDisconnectionHandler(() -> {
            replayOutputStream.close();
            proxyClient.setDisconnectionHandler(null);
            proxyClient.setClientPacketHandler(null);
            proxyClient.getPacketCache().setPacketHandler(null);
        });

        return replayOutputStream;
    }



}
