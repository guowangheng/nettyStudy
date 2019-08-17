package com.easydo.server.handler;

import com.easydo.common.RegisterTable.RegisterTableService;
import com.easydo.common.pojo.Invoker;
import com.easydo.common.pojo.Packager;
import com.easydo.common.pojo.Result;
import com.easydo.server.config.ServerConfig;
import com.easydo.server.server.DynamicServerWrapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

@Component
@ChannelHandler.Sharable
@Slf4j
public class ServerProxyHandler extends SimpleChannelInboundHandler<Packager> {

    @Autowired
    private ServerConfig serverConfig;

    private int idleCount = 1;

    private int count = 1;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("active..");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packager packager) throws Exception {
        log.info("第" + count + "次" + ",服务端接受的消息:" + packager.toString());
        try {
            if (packager.getState() == 1) {
                // 业务处理
                log.info("业务处理..");
                Invoker invoker = (Invoker) packager;
                // 执行方法
                Set<DynamicServerWrapper> serverWrappers = RegisterTableService.getByType(invoker.getClazzName());
                Object object = serverWrappers.iterator().next().getInvoker();
                Method method = object.getClass().getDeclaredMethod(invoker.getMethodName(), invoker.getParameters());
                method.setAccessible(Boolean.TRUE);
                Object data = method.invoke(object, invoker.getParams());
                // 返回数据
                ctx.channel().writeAndFlush(Result.buildResult(Boolean.TRUE, data, null, invoker.getKey()));
            } else if (packager.getState() == 2) {
                // 心跳包
            }
        } catch (Exception e) {
            log.error("处理错误: ", e);
        }
        count++;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server error: ", cause);
        log.error("server restart..");
        serverConfig.start();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.READER_IDLE.equals(event.state())) { // 如果读通道处于空闲状态，说明没有接收到心跳命令
                log.error("已经5秒没有接收到客户端的信息..");
                if (idleCount > 2) {
                    log.error("关闭这个不活跃的channel..");
                    ctx.channel().close();
                }
                idleCount++;
            } else {
                idleCount = 1;
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("server lost active..");
    }

}
