package net.md_5.bungee.connection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PingHandler {
    // TODO this could be useful for a "ping <address>" command or automatic connects to servers to check whether they are full or not
/*
    private final NetworkAddress targetAddress;
    private final Callback<ServerPing> callback;
    private final int protocol;
    private ChannelWrapper channel;

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.channel = channel;
        //MinecraftEncoder encoder = new MinecraftEncoder(Protocol.HANDSHAKE, false, protocol);

        //channel.getHandle().pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.STATUS, false, 578));
        //channel.getHandle().pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, encoder);

        channel.write(new PacketHandshakingInSetProtocol(protocol, this.targetAddress.getHost(), this.targetAddress.getPort(), 1));

        encoder.setProtocol(Protocol.STATUS);
        channel.write(new PacketStatusRequest());
    }

    @Override
    public void exception(Throwable t) throws Exception {
        callback.done(null, t);
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (packet.packet == null) {
            throw new IllegalArgumentException("Unexpected packet received during ping process! " + BufUtil.dump(packet.buf, 16));
        }
    }

    @Override
    public void handle(PacketStatusResponse statusResponse) throws Exception {
        ServerPing serverPing = Util.GSON.fromJson(statusResponse.getResponse(), ServerPing.class);
        callback.done(serverPing, null);
        channel.close();
    }

    @Override
    public String toString() {
        return "[Ping Handler] -> " + this.targetAddress;
    }*/
}
