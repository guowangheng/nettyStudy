package com.easydo.server.config;

import com.easydo.common.RegisterTable.RegisterTableService;
import com.easydo.common.url.URL;
import com.easydo.server.netty.ServerFuture;
import com.easydo.server.server.DynamicServerInvoker;
import com.easydo.server.server.DynamicServerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EasydoServiceBean<T> implements InitializingBean, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, BeanNameAware {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private Class<T> classType;

    private ApplicationContext applicationContext;

    private Map<String, Object> parameterMap;

    @Autowired(required = false)
    private ServerFuture serverFuture;

    private T t;

    // 需要传入接口名
    public EasydoServiceBean(Class<T> classType, Map<String, Object> parameterMap) {
        this.classType = classType;
        this.parameterMap = parameterMap;
    }


    @Override
    public void setBeanName(String s) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext != null) {
            this.applicationContext = applicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            exportService();
        } catch (Exception e) {
            log.error("获取代理对象失败：", e);
        }
    }

    private void exportService() throws InstantiationException, IllegalAccessException {
        this.t = new DynamicServerInvoker<T>(classType.newInstance()).getProxy(classType);
        DynamicServerWrapper serverWrapper = new DynamicServerWrapper();
        serverWrapper.setInvoker(this.t);
        serverWrapper.setUrl(new URL());
        RegisterTableService.setServerTable(classType.getGenericInterfaces()[0].getTypeName(), serverWrapper);
        // 开启netty服务
        ServerConfig serverConfig = applicationContext.getBean(ServerConfig.class);
        try {
            if (this.serverFuture.getChannelFuture() == null) {
                threadPoolTaskExecutor.submit(() -> serverConfig.start()).get();
            }
        } catch (InterruptedException e) {
            log.error("server thread interrupted..");
        } catch (ExecutionException e) {
            log.error("server execute export error..");
        }
    }
}
