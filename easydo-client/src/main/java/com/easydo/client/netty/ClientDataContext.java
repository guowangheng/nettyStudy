package com.easydo.client.netty;

import com.easydo.common.pojo.Result;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Component
@Slf4j
public class ClientDataContext {

    private Map<Long, Result> dataMap = new ConcurrentHashMap<>();

    private AtomicLong dataKey = new AtomicLong(0);

    public Boolean putResult(Long key, Result result) {
        try {
            dataMap.putIfAbsent(key, result);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("put data error..");
        }
        return Boolean.FALSE;
    }

    public Result getResult(Long key) {
        return dataMap.get(key);
    }

    public Long getKey() {
        return dataKey.addAndGet(1L);
    }

}
