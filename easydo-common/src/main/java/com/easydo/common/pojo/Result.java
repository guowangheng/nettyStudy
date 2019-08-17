package com.easydo.common.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@Component
public class Result<T> extends Packager {

    private Boolean success;

    private String message;

    private T data;

    private Long key;

    private CompletableFuture<Result> completeFuture;

    public Result() {

    }

    public static<T> Result<T> buildResult(Boolean success, T t, String message, Long key) {
        Result<T> result = new Result<>();
        result.setKey(key);
        result.setSuccess(success);
        result.setData(t);
        result.setMessage(message);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", key='" + key + '\'' +
                '}';
    }
}
