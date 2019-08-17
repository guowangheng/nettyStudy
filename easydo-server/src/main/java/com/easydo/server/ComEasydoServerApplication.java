package com.easydo.server;

import com.easydo.server.config.ServiceBeanRegisterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan({"com.easydo.server.*"})
@Import(ServiceBeanRegisterConfig.class)
public class ComEasydoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComEasydoServerApplication.class, args);
    }

}
