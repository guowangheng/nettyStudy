package com.easydo.common.annotation.server;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EasydoService {

    int retry() default 0;              // 重试次数

    long timeMsec() default 100000;      // 超时时长

}
