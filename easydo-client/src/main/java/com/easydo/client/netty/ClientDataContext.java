package com.easydo.client.netty;

import com.easydo.common.pojo.Result;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Component
@Slf4j
public class ClientDataContext<T> {

    private Map<Long, CompletableFuture<T>> dataMap = new ConcurrentHashMap<>();

    private AtomicLong dataKey = new AtomicLong(0);

    public Boolean putResult(Long key, CompletableFuture<T> future) {
        try {
            // 第一次存future为空, 直接存就行了, 第二次是要把Future中的Result修改为新的Result
            dataMap.put(key, future);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("put data error..");
        }
        return Boolean.FALSE;
    }

    public CompletableFuture<T> getResult(Long key) {
        return dataMap.get(key);
    }

    public Long getKey() {
        return dataKey.addAndGet(1L);
    }

}
