package com.easydo.client.handler;

import com.easydo.client.config.ClientConfig;
import com.easydo.client.netty.ClientDataContext;
import com.easydo.client.netty.ClientFuture;
import com.easydo.common.pojo.Packager;
import com.easydo.common.pojo.Result;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ChannelHandler.Sharable
@Slf4j
public class ClientProxyHandler extends SimpleChannelInboundHandler<Packager> {

    private int tCount = 1;

    private int count = 1;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private ClientFuture clientFuture;

    @Autowired
    private ClientDataContext clientCxt;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("active..");
        boolean isContains = beanFactory.containsBean("channelHandlerContext");
        if (!isContains) {
            beanFactory.registerSingleton("channelHandlerContext", ctx);
            applicationContext.getAutowireCapableBeanFactory().autowireBean(ctx);
        }
        super.channelActive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packager packager) throws Exception {
        log.info("第" + count + "次" + ",服务端接受的消息:" + packager.toString());
        try {
            if (packager instanceof Result) {
                // 业务处理
                log.info("业务处理..");
                Result result = (Result)packager;
                CompletableFuture future = clientCxt.getResult(result.getKey());
                future.complete(result);
            } else {
                log.error("unKnow data received..");
            }
        } catch (Exception e) {
            log.error("处理错误: ",e);
        }
        count++;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务异常: ",cause);
//        log.error("尝试连接服务... ");
//        clientConfig.start(null,null, clientFuture);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                // 如果空闲,就发送心跳命令,state 2代表心跳
                Packager packager = new Packager(2);
                ctx.channel().writeAndFlush(packager);
                System.out.println("第" + tCount + "次,发送心跳：" + new Date());
                tCount++;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("client lost active..");
        log.error("try connect.. ");
        clientConfig.start(null,null, clientFuture);
    }

}
