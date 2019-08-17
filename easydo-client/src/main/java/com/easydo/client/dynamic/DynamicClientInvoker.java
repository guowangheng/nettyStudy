package com.easydo.client.dynamic;

import com.easydo.client.netty.ClientDataContext;
import com.easydo.common.pojo.Invoker;
import com.easydo.common.pojo.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.CompleteFuture;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DynamicClientInvoker<T> implements InvocationHandler {

    private Invoker<T> invoker;

    private ApplicationContext applicationContext;

    private ClientDataContext clientDataContext;

    public DynamicClientInvoker(Invoker<T> invoker,
                                ApplicationContext applicationContext, ClientDataContext clientDataContext) {
        this.invoker = invoker;
        this.applicationContext = applicationContext;
        this.clientDataContext = clientDataContext;
    }

    public DynamicClientInvoker() {
    }

    public T getProxy(Class<T> classType, ApplicationContext applicationContext,
                      ClientDataContext clientDataContext, Map<String, Object> parameterMap) {
        Invoker<T> invoker = new<T> Invoker();
        invoker.setClassType(classType);
        invoker.setClazzName(classType.getCanonicalName());
        invoker.setState(1);
        invoker.setRetry(Integer.valueOf(parameterMap.get("retry").toString()));
        invoker.setTimeMsec(Long.valueOf(parameterMap.get("timeMsec").toString()));
        ClassLoader classLoader = classType.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{classType},
                new DynamicClientInvoker<T>(invoker, applicationContext, clientDataContext));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 封装invoker
        if (this.invoker == null) {
            // 默认值
            this.invoker = new Invoker();
            this.invoker.setRetry(1);
            this.invoker.setTimeMsec(10000L);
        }
        Long key = clientDataContext.getKey();
        CompletableFuture<Result> future = new CompletableFuture<>();
        this.invoker.setKey(key);
        this.invoker.setMethodName(method.getName());
        this.invoker.setParams(args);
        this.invoker.setParameters(method.getParameterTypes());
        clientDataContext.putResult(key, future);
        // 执行远程调用
        ChannelHandlerContext cxt = this.applicationContext.
                getBean("channelHandlerContext", ChannelHandlerContext.class);
        cxt.channel().writeAndFlush(this.invoker);
        // 获取返回
        Result result = future.get(this.invoker.getTimeMsec(), TimeUnit.MILLISECONDS);
        if (result == null) {
            throw new RuntimeException("method timeout ..");
        }
        return result.getData();
    }

}
