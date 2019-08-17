package com.easydo.server.config;

import com.easydo.server.netty.ServerFuture;
import com.easydo.server.initializer.ServerProxyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * create by: WeanGuo
 * description: netty server
 * create time: 2019/5/27 17:20
 */
@Component
@ConfigurationProperties(prefix = "netty")
public class ServerConfig {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private String host;

    private int port;

    EventLoopGroup bossGroup = new NioEventLoopGroup();

    EventLoopGroup workGroup = new NioEventLoopGroup();

    @Autowired
    private ServerProxyInitializer serverProxyInitializer;

    @Autowired(required = false)
    private ServerFuture serverFuture;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @author     ：WeanGuo
     * @date       ：Created in 2019/5/27 17:20
     * @description：启动netty服务端
     */
    public void start(){
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(serverProxyInitializer);
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            serverFuture.setChannelFuture(channelFuture);
            LOGGER.info("服务端启动成功..");
            //threadPoolTaskExecutor.submit(() -> channelFuture.channel().closeFuture().sync());
        } catch (InterruptedException e) {
            LOGGER.error("server start error: ",e);
        }
    }

    @PreDestroy
    public void destroy(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
