package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.md_5.bungee.netty.PipelineUtils;

import java.net.SocketAddress;

public class ProxyServer {

    private EventLoopGroup bossGroup, workerGroup;

    public void start(SocketAddress address) {
        this.bossGroup = NettyUtils.newEventLoopGroup();
        this.workerGroup = NettyUtils.newEventLoopGroup();

        new ServerBootstrap()
                .channel(NettyUtils.getServerSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(PipelineUtils.SERVER_CHILD)
                .group(this.bossGroup, this.workerGroup)
                .bind(address)
                .syncUninterruptibly();

        System.out.println("Running proxy on " + address);
    }

}
