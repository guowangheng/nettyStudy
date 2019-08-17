package com.easydo.server.service.impl;

import com.easydo.common.annotation.server.EasydoService;
import com.easydo.common.service.TestService;

@EasydoService(retry = 10, timeMsec = 1000L)
public class TestServiceImpl implements TestService {

    @Override
    public String getName(String name) {
        return name + "...";
    }
}
