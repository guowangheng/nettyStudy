package com.easydo.client.test;

import com.easydo.common.annotation.client.EasydoReference;
import com.easydo.common.service.TestService;
import com.easydo.common.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @EasydoReference(retry = 2, timeMsec = 1000)
    private TestService testService;

    @EasydoReference(retry = 3, timeMsec = 1000)
    private UserService userService;

    @RequestMapping("/test")
    public String test() {
        String name = testService.getName("鲍勃");
        int age = userService.getAge(10);
        return name + age;
    }

}
