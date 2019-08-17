package com.easydo.client.config;

import com.easydo.client.initializer.ClientChannelInitializer;
import com.easydo.client.netty.ClientFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * create by: WeanGuo
 * description: netty client
 * create time: 2019/5/27 17:20
 */
@Component
@ConfigurationProperties(prefix = "netty")
@Slf4j
public class ClientConfig {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ClientChannelInitializer clientChannelInitializer;

    private String host;

    private int port;

    EventLoopGroup workGroup = new NioEventLoopGroup();

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
     * @description：启动netty客户端
     * @param bootstrap
     * @param eventLoopGroup
     * @param clientFuture
     */
    public void start(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, final ClientFuture clientFuture){
        try {
            if (bootstrap == null){
                bootstrap = new Bootstrap();
            }
            if (eventLoopGroup == null){
                eventLoopGroup = workGroup;
            }
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                     .option(ChannelOption.SO_KEEPALIVE, true)
                     .handler(clientChannelInitializer)
                     .remoteAddress(this.host,this.port);
            ChannelFuture channelFuture = bootstrap.connect().addListener((ChannelFuture future) -> {
                final EventLoop eventLoop = future.channel().eventLoop();
                if (!future.isSuccess()) {
                    log.error("与服务端断开连接!在10s之后准备尝试重连!");
                    eventLoop.schedule(() -> start(new Bootstrap(), eventLoop, clientFuture), 10, TimeUnit.SECONDS);
                }
            });
            log.info("客户端启动成功..");
            clientFuture.setChannelFuture(channelFuture);
            //threadPoolTaskExecutor.submit(() -> channelFuture.channel().closeFuture().sync());
        } catch (Exception e) {
            log.error("客户端连接失败!",e);
        }
    }

    @PreDestroy
    public void destroy(){
        workGroup.shutdownGracefully();
    }

}
