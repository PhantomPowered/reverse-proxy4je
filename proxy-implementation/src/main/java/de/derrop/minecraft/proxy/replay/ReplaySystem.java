package de.derrop.minecraft.proxy.replay;

public class ReplaySystem { // TODO

/*    private final ExecutorService executorService = Executors.newCachedThreadPool();
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

    public UUID recordReplay(ServiceConnection proxyClient, UUID creatorId, String creatorName) throws IOException, ReplayNotFoundException {
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

        this.executorService.execute(() -> {
            while (!replayInputStream.isClosed()) {
                player.sendPacket(new KeepAlive(System.nanoTime()));
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

            ByteBuf buf = Unpooled.wrappedBuffer(packet.getData());
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

            player.sendPacket(buf);
        }
    }

    public ReplayOutputStream recordReplay(ServiceConnection proxyClient, UUID creatorId, String creatorName, OutputStream outputStream) throws IOException {
        Preconditions.checkArgument(proxyClient.getPacketCache().getPacketHandler() == null, "already recording a replay for that client");

        ReplayInfo replayInfo = new ReplayInfo(proxyClient.getAddress(), creatorId, creatorName, proxyClient.getCredentials(), System.currentTimeMillis(), proxyClient.getEntityId());

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

                DefinedPacket.writeVarInt(Protocol.GAME.TO_CLIENT.getId(definedPacket.getClass(), 47), buf);

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

            replayOutputStream.write(new ReplayPacket(data, System.currentTimeMillis()));
        };
        for (PacketCacheHandler handler : proxyClient.getPacketCache().getHandlers()) {
            handler.sendCached(receiver);
        }
        receiver.sendPacket(new SpawnPlayer(proxyClient.getEntityId(), UUID.fromString("fdef0011-1c58-40c8-bfef-0bdcb1495938"),
                PlayerPositionPacketUtil.getFixLocation(proxyClient.posX), PlayerPositionPacketUtil.getFixLocation(proxyClient.posY), PlayerPositionPacketUtil.getFixLocation(proxyClient.posZ),
                PlayerPositionPacketUtil.getFixRotation(0), PlayerPositionPacketUtil.getFixRotation(0),
                (short) 0,
                Arrays.asList(
                        new DataWatcher.WatchableObject(2, 18, 0, true),
                        new DataWatcher.WatchableObject(3, 17, 0F, true),
                        new DataWatcher.WatchableObject(0, 16, (byte) 0, true),
                        new DataWatcher.WatchableObject(0, 10, (byte) 127, true),
                        new DataWatcher.WatchableObject(0, 9, (byte) 0, true),
                        new DataWatcher.WatchableObject(0, 8, (byte) 0, true),
                        new DataWatcher.WatchableObject(2, 7, 0, true),
                        new DataWatcher.WatchableObject(3, 6, 20F, true),
                        new DataWatcher.WatchableObject(0, 4, (byte) 0, true),
                        new DataWatcher.WatchableObject(0, 3, (byte) 0, true),
                        new DataWatcher.WatchableObject(4, 2, "", true),
                        new DataWatcher.WatchableObject(1, 1, (short) 300, true),
                        new DataWatcher.WatchableObject(0, 0, (byte) 0, true)
                )
        ));
        receiver.sendPacket(proxyClient.getEntityMetadata());

        AtomicBoolean lastOnGround = new AtomicBoolean();
        proxyClient.setClientPacketHandler(packetWrapper -> {
            if (packetWrapper.packet instanceof PlayerPosLook) {
                lastOnGround.set(((PlayerPosLook) packetWrapper.packet).isOnGround());
            } else if (packetWrapper.packet instanceof PlayerLook) {
                lastOnGround.set(((PlayerLook) packetWrapper.packet).isOnGround());
            } else if (packetWrapper.packet instanceof PlayerPosition) {
                lastOnGround.set(((PlayerPosition) packetWrapper.packet).isOnGround());
            }

            if (packetWrapper.packet instanceof PositionedPacket) {
                PositionedPacket packet = (PositionedPacket) packetWrapper.packet;
                receiver.sendPacket(new EntityTeleport(proxyClient.getEntityId(),
                        PlayerPositionPacketUtil.getFixLocation(packet.getX()),
                        PlayerPositionPacketUtil.getFixLocation(packet.getY()),
                        PlayerPositionPacketUtil.getFixLocation(packet.getZ()),
                        PlayerPositionPacketUtil.getFixRotation(packet.getYaw()),
                        PlayerPositionPacketUtil.getFixRotation(packet.getPitch()),
                        lastOnGround.get()
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
    }*/



}
