package com.easydo.client.config;

import com.easydo.client.dynamic.DynamicClientInvoker;
import com.easydo.client.dynamic.DynamicClientWrapper;
import com.easydo.client.netty.ClientDataContext;
import com.easydo.client.netty.ClientFuture;
import com.easydo.common.RegisterTable.RegisterTableService;
import com.easydo.common.url.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EasydoReferenceBean<T> implements ApplicationContextAware, FactoryBean<T> {

    private Class<T> classType;

    private ApplicationContext applicationContext;

    private Map<String, Object> parameterMap;

    private T t;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private ClientFuture clientFuture;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ClientDataContext clientDataContext;

    // 需要传入接口名
    public EasydoReferenceBean(Class<T> classType, Map<String, Object> parameterMap) {
        this.classType = classType;
        this.parameterMap = parameterMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext != null) {
            this.applicationContext = applicationContext;
        }
    }

    @Override
    public T getObject() {
        return refer();
    }

    @Override
    public Class<?> getObjectType() {
        return this.classType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private T refer() {
        try {
            this.t = new DynamicClientInvoker<T>().getProxy(classType, applicationContext, clientDataContext, parameterMap);
        } catch (Exception e) {
            log.error("error: ", e);
        }
        DynamicClientWrapper clientWrapper = new DynamicClientWrapper();
        clientWrapper.setInvoker(this.t);
        clientWrapper.setUrl(new URL());
        // 连接netty服务端
        openClient(clientWrapper);
        return this.t;
    }

    private void openClient(DynamicClientWrapper clientWrapper) {
        if (RegisterTableService.getClientTable().size() > 0) {
            return;
        }
        try {
            if (this.clientFuture.getChannelFuture() == null) {
                threadPoolTaskExecutor.submit(() ->
                        this.clientConfig.start(null, null, clientFuture)).get();
            }
        } catch (InterruptedException e) {
            log.error("thread interrupted..");
        } catch (ExecutionException e) {
            log.error("execute error..");
        }
        RegisterTableService.setClientTable(classType.getCanonicalName(), clientWrapper);
    }

}
