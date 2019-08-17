package com.easydo.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadExecutorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExecutorConfig.class);

    @Value("${spring.task.execution.pool.core-size}")
    private int corePoolSize;

    @Value("${spring.task.execution.pool.max-size}")
    private int maxPoolSize;

    @Value("${spring.task.execution.thread-name-prefix}")
    private String threadNamePrefix;

    @Value("${spring.task.execution.pool.keep-alive}")
    private int keepAliveSeconds;

    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor initExecutor(){
        LOGGER.info("start init Executor...");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(this.corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(this.maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(this.queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(this.threadNamePrefix);
        //设置多长时间，线程回收
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        LOGGER.info("end init Executor...");
        return executor;
    }

}
