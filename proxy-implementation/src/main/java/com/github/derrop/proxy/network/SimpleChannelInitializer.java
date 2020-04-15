package com.github.derrop.proxy.network;

import com.github.derrop.proxy.network.handler.HandlerEndpoint;
import com.github.derrop.proxy.network.length.LengthFrameDecoder;
import com.github.derrop.proxy.network.listener.InitialListener;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.jetbrains.annotations.NotNull;

public final class SimpleChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    public void initChannel(@NotNull Channel channel) {
        channel.config().setOption(ChannelOption.IP_TOS, NetworkUtils.roundedPowDouble(4.899, 2));
        channel.config().setWriteBufferWaterMark(NetworkUtils.WATER_MARK);
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);

        channel.pipeline()
                .addLast(NetworkUtils.TIMEOUT, new ReadTimeoutHandler(15))
                .addLast(NetworkUtils.LENGTH_DECODER, new LengthFrameDecoder())
                .addLast(NetworkUtils.LENGTH_ENCODER, NetworkUtils.LENGTH_FRAME_ENCODER)
                .addLast(NetworkUtils.ENDPOINT, new HandlerEndpoint(null));

        channel.pipeline().get(HandlerEndpoint.class).setChannelListener(new InitialListener());
    }
}
