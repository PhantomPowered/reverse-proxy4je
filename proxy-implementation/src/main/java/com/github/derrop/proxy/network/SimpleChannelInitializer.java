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
package com.github.derrop.proxy.network;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.network.pipeline.handler.HandlerEndpoint;
import com.github.derrop.proxy.network.pipeline.length.LengthFrameDecoder;
import com.github.derrop.proxy.network.listener.InitialListener;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.jetbrains.annotations.NotNull;

public final class SimpleChannelInitializer extends ChannelInitializer<Channel> {

    private final ServiceRegistry registry;

    public SimpleChannelInitializer(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void initChannel(@NotNull Channel channel) {
        channel.config().setOption(ChannelOption.IP_TOS, NetworkUtils.roundedPowDouble(4.899, 2));
        channel.config().setWriteBufferWaterMark(NetworkUtils.WATER_MARK);
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);

        channel.pipeline()
                .addLast(NetworkUtils.TIMEOUT, new ReadTimeoutHandler(15))
                .addLast(NetworkUtils.LENGTH_DECODER, new LengthFrameDecoder())
                .addLast(NetworkUtils.LENGTH_ENCODER, NetworkUtils.LENGTH_FRAME_ENCODER)
                .addLast(NetworkUtils.ENDPOINT, new HandlerEndpoint(this.registry, new InitialListener()));
    }
}
