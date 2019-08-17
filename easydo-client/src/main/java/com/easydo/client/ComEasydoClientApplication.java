package com.easydo.client;

import com.easydo.client.config.ReferenceBeanRegisterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan("com.easydo.*")
@Import({ReferenceBeanRegisterConfig.class})
public class ComEasydoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComEasydoClientApplication.class, args);
    }

}
