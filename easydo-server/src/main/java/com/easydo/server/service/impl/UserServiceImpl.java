package com.easydo.server.service.impl;

import com.easydo.common.annotation.server.EasydoService;
import com.easydo.common.service.UserService;

@EasydoService(retry = 10, timeMsec = 1000L)
public class UserServiceImpl implements UserService {
    @Override
    public int getAge(int age) {
        return age;
    }
}
