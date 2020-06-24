package com.github.derrop.proxy.plugins.gommecw.labyconnect;

import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketDecoder;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketEncoder;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketPrepender;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ClientChannelInitializer
        extends ChannelInitializer<Channel> {
    private final LabyConnection labyConnection;

    public ClientChannelInitializer(LabyConnection labyConnection) {
        this.labyConnection = labyConnection;
    }


    protected void initChannel(Channel channel) throws Exception {
        this.labyConnection.setChannel(channel);
        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(120L, TimeUnit.SECONDS)).addLast("splitter", new PacketPrepender())
                .addLast("decoder", new PacketDecoder()).addLast("prepender", new PacketSplitter()).addLast("encoder", new PacketEncoder())
                .addLast(getLabyConnection());
    }

    public LabyConnection getLabyConnection() {
        return this.labyConnection;
    }
}
