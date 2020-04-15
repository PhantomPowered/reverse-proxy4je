package com.github.derrop.proxy.network;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.network.minecraft.MinecraftDecoder;
import com.github.derrop.proxy.network.minecraft.MinecraftEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public final class ServerConnectionChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) {
        NetworkUtils.BASE.initChannel(channel);

        channel.pipeline()
                .addAfter(NetworkUtils.LENGTH_DECODER, NetworkUtils.PACKET_DECODER, new MinecraftDecoder(ProtocolDirection.TO_SERVER, ProtocolState.HANDSHAKING))
                .addAfter(NetworkUtils.LENGTH_ENCODER, NetworkUtils.PACKET_ENCODER, new MinecraftEncoder());
        // TODO: set initial handler here
    }
}
