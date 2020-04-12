package net.md_5.bungee.connection;

import de.derrop.minecraft.proxy.api.util.NetworkAddress;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BufUtil;
import net.md_5.bungee.ServerPing;
import net.md_5.bungee.Util;
import de.derrop.minecraft.proxy.api.util.Callback;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;

@RequiredArgsConstructor
public class PingHandler extends PacketHandler {

    private final NetworkAddress targetAddress;
    private final Callback<ServerPing> callback;
    private final int protocol;
    private ChannelWrapper channel;

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.channel = channel;
        MinecraftEncoder encoder = new MinecraftEncoder(Protocol.HANDSHAKE, false, protocol);

        channel.getHandle().pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.STATUS, false, 578));
        channel.getHandle().pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, encoder);

        channel.write(new Handshake(protocol, this.targetAddress.getHost(), this.targetAddress.getPort(), 1));

        encoder.setProtocol(Protocol.STATUS);
        channel.write(new StatusRequest());
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
    public void handle(StatusResponse statusResponse) throws Exception {
        ServerPing serverPing = Util.GSON.fromJson(statusResponse.getResponse(), ServerPing.class);
        callback.done(serverPing, null);
        channel.close();
    }

    @Override
    public String toString() {
        return "[Ping Handler] -> " + this.targetAddress;
    }
}
