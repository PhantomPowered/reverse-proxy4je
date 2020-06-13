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
package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.network.ServerConnectionChannelInitializer;
import com.github.derrop.proxy.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

import java.net.SocketAddress;

public class ProxyServer {

    private final MCProxy proxy;

    public ProxyServer(MCProxy proxy) {
        this.proxy = proxy;
    }

    private final EventLoopGroup bossGroup = NettyUtils.newEventLoopGroup();
    private final EventLoopGroup workerGroup = NettyUtils.newEventLoopGroup();

    public void start(SocketAddress address) {
        new ServerBootstrap()
                .channel(NettyUtils.getServerSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ServerConnectionChannelInitializer(this.proxy))
                .group(this.bossGroup, this.workerGroup)
                .bind(address)
                .syncUninterruptibly();

        System.out.println("Running proxy on " + address);
    }

}
