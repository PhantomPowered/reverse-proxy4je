package de.derrop.minecraft.proxy.replay;

import com.google.common.base.Preconditions;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.connection.cache.handler.LoginCache;
import de.derrop.minecraft.proxy.connection.cache.packet.entity.spawn.SpawnPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.connection.PacketReceiver;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ReplaySystem {

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

    public UUID recordReplay(ConnectedProxyClient proxyClient, UUID creatorId, String creatorName) throws IOException, ReplayNotFoundException {
        UUID replayId = UUID.randomUUID();

        OutputStream outputStream = Files.newOutputStream(this.replayDirectory.resolve(replayId + ".replay"));

        this.recordReplay(proxyClient, creatorId, creatorName, outputStream);

        return replayId;
    }

    public CompletableFuture<Boolean> playReplayAsync(UserConnection player, UUID replayId, Consumer<ReplayInfo> infoConsumer) throws ReplayNotFoundException {
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

    public void playReplay(UserConnection player, UUID replayId, Consumer<ReplayInfo> infoConsumer) throws IOException, ReplayNotFoundException {
        InputStream inputStream = Files.newInputStream(this.getPath(replayId));

        this.playReplay(player, inputStream, infoConsumer);
    }

    public void playReplay(UserConnection player, InputStream inputStream, Consumer<ReplayInfo> infoConsumer) throws IOException {
        ReplayInputStream replayInputStream = new ReplayInputStream(inputStream);

        infoConsumer.accept(replayInputStream.getReplayInfo());

        long recBegin = replayInputStream.getReplayInfo().getTimestamp();
        long playBegin = System.currentTimeMillis();

        ReplayPacket packet;
        while ((packet = replayInputStream.readPacket()) != null) {
            long targetTime = (packet.getTimestamp() - recBegin) + playBegin;
            if (System.currentTimeMillis() < targetTime) {
                try {
                    Thread.sleep(targetTime - System.currentTimeMillis());
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }

            player.getCh().write(Unpooled.wrappedBuffer(packet.getData()));
        }
    }

    public ReplayOutputStream recordReplay(ConnectedProxyClient proxyClient, UUID creatorId, String creatorName, OutputStream outputStream) throws IOException {
        Preconditions.checkArgument(proxyClient.getPacketCache().getPacketHandler() == null, "already recording a replay for that client");

        ReplayInfo replayInfo = new ReplayInfo(proxyClient.getAddress(), creatorId, creatorName, proxyClient.getCredentials(), System.currentTimeMillis());

        ReplayOutputStream replayOutputStream = new ReplayOutputStream(replayInfo, this.executorService, outputStream);

        this.runningReplays.put(replayInfo, replayOutputStream);

        replayOutputStream.setCloseHandler(() -> {
            this.runningReplays.remove(replayInfo);
            proxyClient.getPacketCache().setPacketHandler(null);
            proxyClient.setDisconnectionHandler(null);
        });

        PacketReceiver receiver = packet -> {
            if (replayOutputStream.isClosed()) {
                return;
            }

            ByteBuf buf;

            if (packet instanceof DefinedPacket) {
                buf = Unpooled.buffer();

                DefinedPacket definedPacket = (DefinedPacket) packet;

                Protocol.DirectionData prot = Protocol.GAME.TO_CLIENT;
                DefinedPacket.writeVarInt(prot.getId(definedPacket.getClass(), 47), buf);

                ((DefinedPacket) packet).write(buf, ProtocolConstants.Direction.TO_CLIENT, 47);
            } else if (packet instanceof PacketWrapper) {
                buf = ((PacketWrapper) packet).buf.copy();
            } else if (packet instanceof ByteBuf) {
                buf = ((ByteBuf) packet).copy();
            } else {
                throw new IllegalArgumentException("Invalid packet received");
            }

            buf.markReaderIndex();

            int packetId = DefinedPacket.readVarInt(buf);

            buf.resetReaderIndex();

            if (!ReplayIDs.isReplayPacket(packetId)) {
                return;
            }

            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);

            replayOutputStream.write(new ReplayPacket(data, replayInfo.getTimestamp()));
        };
        for (PacketCacheHandler handler : proxyClient.getPacketCache().getHandlers()) {
            handler.sendCached(receiver);
        }

        receiver.sendPacket(new SpawnPlayer(proxyClient.getEntityId(), proxyClient.getAccountUUID(), proxyClient.posX, proxyClient.posY, proxyClient.posZ, (byte) 0, (byte) 0, (short) 0, new ArrayList<>()));

        proxyClient.setClientPacketHandler(packetWrapper -> receiver.sendPacket(packetWrapper.buf));
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
